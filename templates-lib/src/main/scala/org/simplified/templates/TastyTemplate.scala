package org.simplified.templates

import scala.collection.mutable
import scala.quoted.*
import dotty.tools.dotc.ast.Trees.*

import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.*

private class TastyTemplateInspector extends Inspector:

  override def inspect(using Quotes)(tastys: List[Tasty[quotes.type]]): Unit =
    for tasty <- tastys do
      val ctx                                      = scala.quoted.quotes.asInstanceOf[scala.quoted.runtime.impl.QuotesImpl].ctx
      given dotty.tools.dotc.core.Contexts.Context = ctx
      tasty.ast match
        case PackageDef(pid, stats) =>
          val types = stats.collect:
            case TypeDef(typeName, Template(constr, parentsOrDerived, self, preBody: List[_])) =>
              def paramsCode(param: Any) = param match
                case v @ ValDef(name, tpt, preRhs) =>
                  ValDef(s"x$name", tpt, preRhs)

              val methods = preBody.collect:
                case d @ DefDef(name, paramss: List[List[_]] @unchecked, tpt, preRhs) if !name.toString.contains("$") =>
                  d

/** Converts tasty files to an easier to digest domain model
  */
class TastyTemplate:
  def apply(tastyFiles: List[String]) =
    val inspector = new TastyTemplateInspector
    TastyInspector.inspectTastyFiles(tastyFiles)(inspector)

object TastyTemplate:
  def apply() = new TastyTemplate
