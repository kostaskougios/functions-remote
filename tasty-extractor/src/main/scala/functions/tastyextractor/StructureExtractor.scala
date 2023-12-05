package functions.tastyextractor

import functions.model.GeneratorConfig
import functions.tastyextractor.model.{DetectedCatsEffect, EImport, EMethod, EPackage, EParam, EType}

import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.*
import dotty.tools.dotc.ast.untpd.ImportSelector

private class StructureExtractorInspector extends Inspector:
  val packages = mutable.ListBuffer.empty[EPackage]

  override def inspect(using Quotes)(tastys: List[Tasty[quotes.type]]): Unit =
    import quotes.reflect.*

    def eTypeOf(tpe: TypeRepr): EType = tpe match
      case t: TypeRef      =>
        val name = t.name
        val code = t.show
        EType.code(name, code)
      case at: AppliedType =>
        val args  = at.args.map(eTypeOf)
        val tycon = at.tycon
        EType(tycon.typeSymbol.name, tpe.show, args, None, None, Nil)
      case c: ConstantType =>
        // not sure if this works:
        EType(c.typeSymbol.name, tpe.show, Nil, None, None, Nil)

    object MethodTraverser extends TreeAccumulator[List[EMethod]]:
      def foldTree(existing: List[EMethod], tree: Tree)(owner: Symbol): List[EMethod] =
        def paramsCode(param: Any) =
          param match
            case v: ValDef @unchecked =>
              val tpe = eTypeOf(v.tpt.tpe)
              EParam(v.name, tpe, s"${v.name} : ${v.tpt.show}")

        val r = tree match
          case d: DefDef if !d.name.contains("$") && d.name != "<init>" =>
            BetterErrors.betterError(s"Error while parsing. Owner: $owner method: ${d.show}"):
              val m = EMethod(d.name, d.paramss.map(pc => pc.params.map(paramsCode)), eTypeOf(d.returnTpt.tpe), d.symbol.docstring)
              List(m)
          case _                                                        =>
            Nil
        foldOverTree(existing ++ r, tree)(owner)
    end MethodTraverser

    object TypeTraverser extends TreeAccumulator[List[EType]]:
      private def detectCats(c: ClassDef) =
        c.body.take(2) match
          case Seq(TypeDef(name, _), ValDef(_, typeTree, _)) =>
            typeTree.tpe match
              case AppliedType(TypeRef(repr, catsClassName), typeReprs) if repr.show.startsWith("cats.effect") =>
                val fullCatsClassName = repr.show + "." + catsClassName
                Some(DetectedCatsEffect(name, fullCatsClassName, catsClassName))
              case _                                                                                           => None
          case _                                             => None

      def foldTree(existing: List[EType], tree: Tree)(owner: Symbol): List[EType] =
        val r = tree match
          case c: ClassDef =>
            val frameworks = detectCats(c)
            val methods    = MethodTraverser.foldTree(Nil, c)(owner)
            val t          = EType(c.name, c.name, Nil, frameworks, c.symbol.docstring, methods)
            List(t)
          case _           =>
            Nil
        foldOverTree(existing ++ r, tree)(owner)
    end TypeTraverser

    object ImportTraverser extends TreeAccumulator[List[EImport]]:
      def foldTree(existing: List[EImport], tree: Tree)(owner: Symbol): List[EImport] =
        val r = tree match
          case Import(module, paths) =>
            for case ImportSelector(ident, _, _) <- paths
            yield EImport(module.show + "." + ident.name.toString)

          case _ => Nil
        foldOverTree(existing ++ r, tree)(owner)
    end ImportTraverser

    object PackageTraverser extends TreeAccumulator[List[EPackage]]:
      def foldTree(existing: List[EPackage], tree: Tree)(owner: Symbol): List[EPackage] =
        val r = tree match
          case p: PackageClause =>
            val types   = TypeTraverser.foldTree(Nil, p)(owner)
            val imports = ImportTraverser.foldTree(Nil, p)(owner)
            val t       = EPackage(p.pid.show, imports, types)
            List(t)
          case _                =>
            Nil
        foldOverTree(existing ++ r, tree)(owner)
    end PackageTraverser

    for tasty <- tastys do
      val tree = tasty.ast
      packages ++= PackageTraverser.foldTree(Nil, tree)(tree.symbol)

/** Converts tasty files to an easier to digest domain model
  */
class StructureExtractor:
  def fromJars(jars: List[String]): Seq[EPackage] =
    val inspector = new StructureExtractorInspector
    TastyInspector.inspectAllTastyFiles(Nil, List(jars.head), jars.tail)(inspector)

    for
      p <- inspector.packages.toSeq
      t <- p.types if t.scalaDocs.exists(_.contains("//> exported"))
    yield p.copy(types = List(t))

  def forDependency(generatorConfig: GeneratorConfig, dep: String): Seq[EPackage] =
    val jars = generatorConfig.exportJar(dep)
    fromJars(jars.toList)

object StructureExtractor:
  def apply() = new StructureExtractor

@main def trySE() =
  val packages = StructureExtractor().forDependency(GeneratorConfig.withDefaults(), "com.example:ls-exports_3:0.1-SNAPSHOT")
  println(packages.mkString("\n"))
