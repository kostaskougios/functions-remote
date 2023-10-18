package functions.proxygenerator.codegenerators.model

import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.tastyextractor.model.{DetectedCatsEffect, EMethod, EType}
import mustache.integration.model.{Many, Param, Params}

import scala.language.implicitConversions

case class Func(
    functionN: String,
    params: String,
    paramsCall: String,
    paramsRaw: Many[Param],
    resultN: String,
    resultNNoFramework: String,
    mapResult: Boolean,
    caseClass: String,
    caseClassName: String,
    last: Boolean
)

object Func:
  def apply(`type`: EType, methodToCaseClassNamingConventions: GenericTypeGenerator.NamingConventions): Seq[Func] =
    val last = `type`.methods.last
    `type`.methods.map: m =>
      val params             = toParams(m)
      val caseClassName      = methodToCaseClassNamingConventions.methodArgsCaseClassName(`type`, m)
      val rTpe               = m.returnType
      val resultN            = rTpe.simplifiedCode
      val resultNNoFramework = `type`.typeNoFramework(rTpe).simplifiedCode
      Func(
        m.name,
        params.toMethodDeclArguments,
        params.toMethodCallArguments,
        params.params,
        resultN,
        resultNNoFramework,
        `type`.isFrameworkType(rTpe),
        methodToCaseClassNamingConventions.caseClassHolderObjectName(`type`) + "." + caseClassName,
        caseClassName,
        m eq last
      )

  private def toParams(m: EMethod): Params = {
    val paramsFlat = m.paramss.flatten
    val last       = paramsFlat.lastOption
    Params(paramsFlat.map(ep => Param(ep.name, ep.`type`.simplifiedCode, last.isEmpty || ep.eq(last.get))))
  }
