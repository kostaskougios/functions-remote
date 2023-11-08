package functions.proxygenerator.codegenerators.http4s

import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
import functions.proxygenerator.codegenerators.model.Func
import functions.tastyextractor.model.{EMethod, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.{Param, ResourceTemplatesSourceLocation}

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
            toHttp4sRouteType(p)
          .mkString(" / "),
        httpMethod(method)
      )
  )

  private def httpMethod(method: EMethod) =
    method.scalaDocs match
      case Some(d) if d.contains("//> HTTP-GET")     => "GET"
      case Some(d) if d.contains("//> HTTP-POST")    => "POST"
      case Some(d) if d.contains("//> HTTP-PUT")     => "PUT"
      case Some(d) if d.contains("//> HTTP-HEAD")    => "HEAD"
      case Some(d) if d.contains("//> HTTP-DELETE")  => "DELETE"
      case Some(d) if d.contains("//> HTTP-CONNECT") => "CONNECT"
      case Some(d) if d.contains("//> HTTP-OPTIONS") => "OPTIONS"
      case Some(d) if d.contains("//> HTTP-TRACE")   => "TRACE"
      case Some(d) if d.contains("//> HTTP-PATCH")   => "PATCH"
      case _                                         => "PUT"

  private def toHttp4sRouteType(p: Param) = p.`type` match
    case "Int"    => s"IntVar(${p.name})"
    case "Long"   => s"LongVar(${p.name})"
    case "String" => p.name
    case x        => throw new IllegalArgumentException(s"Not yet supported type: $x")

case class RoutesExtras(
    hasHttpArgs: Boolean,
    urlArgs: String,
    httpMethod: String
)
