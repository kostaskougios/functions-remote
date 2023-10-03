package functions.tastyextractor

import functions.model.GeneratorConfig
import functions.tastyextractor.model.{EMethod, EPackage, EParam, EType}

import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.*

private class StructureExtractorInspector extends Inspector:
  val packages = mutable.ListBuffer.empty[EPackage]

  override def inspect(using Quotes)(tastys: List[Tasty[quotes.type]]): Unit =
    import quotes.reflect.*
    object MethodTraverser extends TreeAccumulator[List[EMethod]]:
      def foldTree(existing: List[EMethod], tree: Tree)(owner: Symbol): List[EMethod] =
        def paramsCode(param: Any) =
          param match
            case v: ValDef @unchecked =>
              EParam(v.name, v.tpt.show, s"${v.name} : ${v.tpt.show}")

        val r = tree match
          case d: DefDef if !d.name.contains("$") && d.name != "<init>" =>
            val m = EMethod(d.name, d.paramss.map(pc => pc.params.map(paramsCode)), EType.code(d.returnTpt.symbol.name.toString, d.returnTpt.show))
            List(m)
          case _                                                        =>
            Nil
        foldOverTree(existing ++ r, tree)(owner)
    end MethodTraverser

    object TypeTraverser extends TreeAccumulator[List[EType]]:
      def foldTree(existing: List[EType], tree: Tree)(owner: Symbol): List[EType] =
        val r = tree match
          case c: ClassDef =>
            val methods = MethodTraverser.foldTree(Nil, c)(owner)
            val t       = EType(c.name, c.name, c.symbol.docstring, methods)
            List(t)
          case _           =>
            Nil
        foldOverTree(existing ++ r, tree)(owner)
    end TypeTraverser

    object PackageTraverser extends TreeAccumulator[List[EPackage]]:
      def foldTree(existing: List[EPackage], tree: Tree)(owner: Symbol): List[EPackage] =
        val r = tree match
          case p: PackageClause =>
            val types = TypeTraverser.foldTree(Nil, p)(owner)
            val t     = EPackage(p.pid.show, types)
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
    TastyInspector.inspectAllTastyFiles(Nil, jars, Nil)(inspector)

    for
      p <- inspector.packages.toSeq
      t <- p.types if t.scalaDocs.exists(_.contains("//> exported"))
    yield p.copy(types = List(t))

  def forDependency(generatorConfig: GeneratorConfig, dep: String): Seq[EPackage] =
    val jar = generatorConfig.exportJar(dep)
    fromJars(List(jar))

object StructureExtractor:
  def apply() = new StructureExtractor

@main def trySE() =
  val packages = StructureExtractor().forDependency(GeneratorConfig.withDefaults(), "com.example:ls-exports_3:0.1-SNAPSHOT")
  println(packages.mkString("\n"))
