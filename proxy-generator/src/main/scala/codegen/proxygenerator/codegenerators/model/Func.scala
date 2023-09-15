package codegen.proxygenerator.codegenerators.model

import codegen.proxygenerator.codegenerators.{GenericTypeGenerator, MethodToCaseClassGenerator}
import codegen.tastyextractor.model.EType
import mustache.integration.model.{Many, Param}

import scala.language.implicitConversions

case class Func(functionN: String, params: String, paramsCall: String, paramsRaw: Many[Param], resultN: String, caseClass: String, caseClassName: String)

object Func:
  def apply(`type`: EType, methodToCaseClassNamingConventions: GenericTypeGenerator.NamingConventions): Seq[Func] =
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
