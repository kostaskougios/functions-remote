package functions.proxygenerator.codegenerators.model

import functions.proxygenerator.codegenerators.{GenericTypeGenerator, MethodToCaseClassGenerator}
import functions.tastyextractor.model.{EMethod, EType}
import mustache.integration.model.{Many, Param, Params}

import scala.language.implicitConversions

case class Func(
    functionN: String,
    params: String,
    paramsCall: String,
    paramsRaw: Many[Param],
    resultN: String,
    caseClass: String,
    caseClassName: String,
    last: Boolean
)

object Func:
  def apply(`type`: EType, methodToCaseClassNamingConventions: GenericTypeGenerator.NamingConventions): Seq[Func] =
    val last = `type`.methods.last
    `type`.methods.map: m =>
      val params        = toParams(m)
      val caseClassName = methodToCaseClassNamingConventions.methodArgsCaseClassName(`type`, m)
      Func(
        m.name,
        params.toMethodDeclArguments,
        params.toMethodCallArguments,
        params.params,
        m.returnType.code,
        methodToCaseClassNamingConventions.caseClassHolderObjectName(`type`) + "." + caseClassName,
        caseClassName,
        m eq last
      )

  private def toParams(m: EMethod): Params = {
    val paramsFlat = m.paramss.flatten
    val last       = paramsFlat.lastOption
    Params(paramsFlat.map(ep => Param(ep.name, ep.`type`.typeUnqualified, last.isEmpty || ep.eq(last.get))))
  }
