package codegen.tastyextractor

import codegen.model.GeneratorConfig
import codegen.tastyextractor.model.{EMethod, EPackage, EParam, EType}
import codegen.tastyextractor.utils.Cleanup.*
import dotty.tools.dotc.ast.Trees.*

import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.*

private class StructureExtractorInspector extends Inspector:
  val packages = mutable.ListBuffer.empty[EPackage]

  override def inspect(using Quotes)(tastys: List[Tasty[quotes.type]]): Unit =
    for tasty <- tastys do
      val ctx                                      = scala.quoted.quotes.asInstanceOf[scala.quoted.runtime.impl.QuotesImpl].ctx
      given dotty.tools.dotc.core.Contexts.Context = ctx
      tasty.ast match {
        case PackageDef(pid, stats) =>
          val types = stats.collect { case TypeDef(typeName, Template(constr, parentsOrDerived, self, preBody: List[_])) =>
            def paramsCode(param: Any) = param match
              case ValDef(name, tpt, preRhs) =>
                EParam(name.show, removeColours(tpt.show), removeColours(s"$name : ${tpt.show}"))

            val methods = preBody.collect {
              case DefDef(name, paramss: List[List[_]] @unchecked, tpt, preRhs) if !name.toString.contains("$") =>
                EMethod(name.toString, paramss.map(_.map(paramsCode)), EType.code(tpt.symbol.name.toString, removeColours(tpt.show)))
            }
            EType(typeName.toString, typeName.show, methods)
          }
          packages += model.EPackage(pid.name.show, types)
      }

/** Converts tasty files to an easier to digest domain model
  */
class StructureExtractor:
  def apply(tastyFiles: List[String]): Seq[EPackage] =
    val inspector = new StructureExtractorInspector
    TastyInspector.inspectTastyFiles(tastyFiles)(inspector)
    inspector.packages.toSeq

  def fromJar(tastyFile: String, jar: String): Seq[EPackage] = fromJars(List(tastyFile), List(jar))

  def fromJars(tastyFiles: List[String], jars: List[String]): Seq[EPackage] =
    val inspector = new StructureExtractorInspector
    TastyInspector.inspectAllTastyFiles(tastyFiles, jars, Nil)(inspector)
    inspector.packages.toSeq

  def forDependency(generatorConfig: GeneratorConfig, dep: String): Seq[EPackage] = ???

object StructureExtractor:
  def apply() = new StructureExtractor
