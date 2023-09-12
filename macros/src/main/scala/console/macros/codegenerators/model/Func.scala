package console.macros.codegenerators.model

import console.macros.codegenerators.MethodToCaseClassGenerator
import console.macros.model.{EMethod, EType}
import mustache.integration.model.{Many, Param}
import scala.language.implicitConversions

case class Func(functionN: String, params: String, paramsCall: String, paramsRaw: Many[Param], resultN: String, caseClass: String, caseClassName: String)

object Func:
  def apply(`type`: EType, methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions): Seq[Func] =
    `type`.methods.map: m =>
      val params        = m.toParams
      val caseClassName = methodToCaseClassNamingConventions.methodArgsCaseClassName(`type`, m)
      Func(
        m.name,
        params.toMethodDeclArguments,
        params.toMethodCallArguments,
        params.params,
        m.returnType.name,
        methodToCaseClassNamingConventions.caseClassHolderObjectName(`type`) + "." + caseClassName,
        caseClassName
      )
