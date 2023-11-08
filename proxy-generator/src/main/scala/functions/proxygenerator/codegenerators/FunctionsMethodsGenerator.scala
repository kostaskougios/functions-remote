package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators.http4s.RoutesGenerator
import functions.tastyextractor.model.{EMethod, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.{Many, ResourceTemplatesSourceLocation}

import scala.language.implicitConversions

object FunctionsMethodsGenerator:

  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = methodParamsTraitName(`type`)

  def apply(
      namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions
  ) = new GenericTypeGenerator(
    "MethodToCaseClass",
    namingConventions,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsMethods"),
    extras
  )

  private def extras(`type`: EType, method: EMethod): FunctionsMethodsExtras =
    FunctionsMethodsExtras(
      RoutesGenerator.httpMethod(method).toList.map(m => CoordProperty("HTTP-METHOD", m))
    )

case class FunctionsMethodsExtras(
    coordinatesProperties: Seq[CoordProperty]
):
  def coordinatesPropertiesValues: String = coordinatesProperties.map(p => s""""${p.name}" -> "${p.value}"""").mkString(", ")

case class CoordProperty(name: String, value: String)
