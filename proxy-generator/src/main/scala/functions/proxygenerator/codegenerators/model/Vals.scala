package functions.proxygenerator.codegenerators.model

import functions.tastyextractor.model.{DetectedCatsEffect, EType}
import mustache.integration.model.{GeneratorFactories, Many}

case class Vals(
    exportedType: EType,
    proxypackage: String,
    imports: Many[String],
    frameworkImports: Many[String],
    allImports: Many[String],
    className: String,
    methodParams: String,
    functions: Many[Func],
    generatorFactories: GeneratorFactories
):
  def exportedTypeTypeArgs: String = if exportedType.hasFramework then s"[$frameworkTypeArg]" else ""
  def exportedTypeFull: String     = exportedType.name + exportedTypeTypeArgs
  def frameworkTypeArg: String     = exportedType.framework
    .map:
      case ce: DetectedCatsEffect => ce.typeArg
    .getOrElse("")

  def frameworkTypeArgOpen: String  = if exportedType.hasFramework then frameworkTypeArg + "[" else ""
  def frameworkTypeArgClose: String = if exportedType.isCatsEffect then "]" else ""
  def frameworkTypeArgFull: String  = exportedType.framework
    .map { case ce: DetectedCatsEffect =>
      s"[${ce.typeArg}[_] : ${ce.catsClassName}]"
    }
    .getOrElse("")
