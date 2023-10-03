//package examples
//
//import scala.quoted.*
//import scala.tasty.inspector.*
//import codegen.tastyextractor.model.*
//
//class MyInspector extends Inspector:
//  def inspect(using Quotes)(tastys: List[Tasty[quotes.type]]): Unit =
//    import quotes.reflect.*
//    object MethodTraverser extends TreeAccumulator[List[EMethod]]:
//      def foldTree(existing: List[EMethod], tree: Tree)(owner: Symbol): List[EMethod] =
//        def paramsCode(param: Any) = param match
//          case ValDef(name, tpt, preRhs) =>
//            EParam(name, tpt.show, s"$name : ${tpt.show}")
//
//        val r = tree match
//          case d: DefDef if !d.name.contains("$") && d.name != "<init>" =>
//            val m = EMethod(d.name, d.paramss.map(pc => pc.params.map(paramsCode)), EType.code(d.returnTpt.symbol.name.toString, d.returnTpt.show))
//            List(m)
//          case tree                                                     =>
//            Nil
//        foldOverTree(existing ++ r, tree)(owner)
//    end MethodTraverser
//
//    object TypeTraverser extends TreeAccumulator[List[EType]]:
//      def foldTree(existing: List[EType], tree: Tree)(owner: Symbol): List[EType] =
//        val r = tree match
//          case c: ClassDef =>
//            val methods = MethodTraverser.foldTree(Nil, c)(owner)
//            val t       = EType(c.name, c.name, methods)
//            List(t)
//          case tree        =>
//            Nil
//        foldOverTree(existing ++ r, tree)(owner)
//    end TypeTraverser
//
//    object PackageTraverser extends TreeAccumulator[List[EPackage]]:
//      def foldTree(existing: List[EPackage], tree: Tree)(owner: Symbol): List[EPackage] =
//        val r = tree match
//          case p: PackageClause =>
//            val types = TypeTraverser.foldTree(Nil, p)(owner)
//            val t     = EPackage(p.symbol.name, types)
//            List(t)
//          case tree             =>
//            Nil
//        foldOverTree(existing ++ r, tree)(owner)
//    end PackageTraverser
//
//    tastys
//      .flatMap { tasty =>
//        val tree = tasty.ast
//        PackageTraverser.foldTree(List.empty, tree)(tree.symbol)
//      }
//      .foreach(println)
//
//@main def runExample() =
//  val inspector = new MyInspector
//  TastyInspector.inspectAllTastyFiles(List("../example-commands/ls-exports/target/scala-3.3.1/classes/ls/LsFunctions.tasty"), Nil, Nil)(inspector)
