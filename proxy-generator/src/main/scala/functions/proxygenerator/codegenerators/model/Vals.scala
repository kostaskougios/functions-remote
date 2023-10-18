package functions.proxygenerator.codegenerators.model

import functions.tastyextractor.model.{DetectedCatsEffect, EType}
import mustache.integration.model.Many

case class Vals(
    exportedType: EType,
    proxypackage: String,
    imports: Many[String],
    className: String,
    methodParams: String,
    functions: Many[Func]
):
  def exportedTypeTypeArgs: String = if exportedType.isCatsEffect then s"[$frameworkTypeArg]" else ""
  def exportedTypeFull: String     = exportedType.name + exportedTypeTypeArgs
  def frameworkTypeArg: String     = exportedType.framework
    .map:
      case ce: DetectedCatsEffect => ce.typeArg
    .getOrElse("")

  def frameworkTypeArgOpen: String  = frameworkTypeArg + "["
  def frameworkTypeArgClose: String = if exportedType.isCatsEffect then "]" else ""
  def frameworkTypeArgFull: String  = exportedType.framework
    .map { case ce: DetectedCatsEffect =>
      s"[${ce.typeArg}[_] : ${ce.catsClassName}]"
    }
    .getOrElse("")
