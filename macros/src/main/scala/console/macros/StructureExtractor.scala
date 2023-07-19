package console.macros

import console.macros.model.*
import console.macros.utils.Cleanup
import console.macros.utils.Cleanup.removeColours
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.config.Printers.Printer
import dotty.tools.dotc.core.Decorators.show
import dotty.tools.dotc.core.Names.{TermName, TypeName, typeName}
import dotty.tools.dotc.printing.PlainPrinter
import org.apache.commons.lang3.StringUtils

import scala.collection.mutable
import scala.io.AnsiColor
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
          packages += EPackage(pid.name.show, types)
      }

/** Converts tasty files to an easier to digest domain model
  */
class StructureExtractor:
  def apply(tastyFiles: List[String]): Seq[EPackage] =
    val inspector = new StructureExtractorInspector
    TastyInspector.inspectTastyFiles(tastyFiles)(inspector)
    inspector.packages.toSeq

object StructureExtractor:
  def apply() = new StructureExtractor
