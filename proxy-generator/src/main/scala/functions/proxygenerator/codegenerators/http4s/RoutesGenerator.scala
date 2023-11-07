package functions.proxygenerator.codegenerators.http4s

import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
import functions.proxygenerator.codegenerators.model.Func
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object RoutesGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}Http4sRoutes"

  def apply(
      namingConventions: NamingConventions = DefaultNamingConventions
  ) = new GenericTypeGenerator(
    "Http4sRoutes",
    namingConventions,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.http4s.Routes"),
    (tpe, method) =>
      val (httpArgs, serializableArgs) = Func.toParams(method)
      RoutesExtras(
        httpArgs.params.nonEmpty,
        httpArgs.params
          .map: p =>
            toHttp4sRouteType(p.`type`) + "(" + p.name + ")"
          .mkString(" / ")
      )
  )
  private def toHttp4sRouteType(tpe: String) = tpe match
    case "Int" => "IntVar"
    case x     => throw new IllegalArgumentException(s"Not yet supported type: $x")

case class RoutesExtras(
    hasHttpArgs: Boolean,
    urlArgs: String
)
