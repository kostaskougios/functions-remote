package functions.proxygenerator.codegenerators.model

import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.tastyextractor.model.{DetectedCatsEffect, EMethod, EType}
import mustache.integration.model.{Many, Param, Params}

import scala.language.{implicitConversions, reflectiveCalls}

case class Func(
    functionN: String,
    firstParams: String,
    firstParamsCall: String,
    firstParamsRaw: Many[Param],
    params: String,
    paramsCall: String,
    paramsRaw: Many[Param],
    returnType: EType,
    resultN: String,
    resultNNoFramework: String,
    mapResults: Boolean,
    caseClass: String,
    caseClassName: String,
    last: Boolean,
    extras: Any
):
  def isUnitReturnType: Boolean = resultNNoFramework == "Unit"
  def firstParamsAndParens      = if firstParamsRaw.isEmpty then "" else s"($firstParams)"
  def firstParamsCallAndParens  = if firstParamsRaw.isEmpty then "" else s"($firstParamsCall)"

object Func:
  type ExtrasFunction = (EType, EMethod) => Any
  def apply(`type`: EType, methodToCaseClassNamingConventions: GenericTypeGenerator.NamingConventions, extrasCreator: ExtrasFunction): Seq[Func] =
    val last = `type`.methods.last
    `type`.methods.map: m =>
      val (firstParams, secondParams) = toParams(m)
      val caseClassName               = methodToCaseClassNamingConventions.methodArgsCaseClassName(`type`, m)
      val rTpe                        = m.returnType
      val resultN                     = rTpe.simplifiedCode
      val resultNNoFramework          = `type`.typeNoFramework(rTpe).simplifiedCode
      val mapResults                  = `type`.isFrameworkType(rTpe)
      Func(
        m.name,
        firstParams.toMethodDeclArguments,
        firstParams.toMethodCallArguments,
        firstParams.params,
        secondParams.toMethodDeclArguments,
        secondParams.toMethodCallArguments,
        secondParams.params,
        rTpe,
        resultN,
        resultNNoFramework,
        mapResults,
        methodToCaseClassNamingConventions.caseClassHolderObjectName(`type`) + "." + caseClassName,
        caseClassName,
        m eq last,
        extrasCreator(`type`, m)
      )

  def toParams(m: EMethod) =
    val (firstParams, secondParams) = m.paramss match
      case List(ps)     => (Nil, ps)
      case List(p1, p2) => (p1, p2)
      case _            => throw new IllegalArgumentException(s"Method ${m.name} has more than 2 sets of params which is not supported")
    val fpLast                      = firstParams.lastOption
    val fps                         = Params(firstParams.map(ep => Param(ep.name, ep.`type`.simplifiedCode, fpLast.isEmpty || ep.eq(fpLast.get))))

    val spLast = secondParams.lastOption
    val sps    = Params(secondParams.map(ep => Param(ep.name, ep.`type`.simplifiedCode, spLast.isEmpty || ep.eq(spLast.get))))
    (fps, sps)
