package functions.proxygenerator.codegenerators.helidon

import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
import functions.proxygenerator.codegenerators.model.Func
import functions.tastyextractor.model.{EMethod, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.{Many, ResourceTemplatesSourceLocation}

object HelidonRoutesGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}HelidonRoutes"

  def apply(
      namingConventions: NamingConventions = DefaultNamingConventions
  ) = new GenericTypeGenerator(
    "HelidonRoutes",
    namingConventions,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.helidon.Routes"),
    (tpe, method) =>
      val (httpArgs, serializableArgs) = Func.toParams(method)
      RoutesExtras(
        httpArgs.params.map(p => ReqParam(p.name)),
        httpMethod(method).getOrElse("PUT")
      )
  )

  def httpMethod(method: EMethod): Option[String] =
    method.scalaDocs match
      case Some(d) if d.contains("//> HTTP-GET")     => Some("GET")
      case Some(d) if d.contains("//> HTTP-POST")    => Some("POST")
      case Some(d) if d.contains("//> HTTP-PUT")     => Some("PUT")
      case Some(d) if d.contains("//> HTTP-HEAD")    => Some("HEAD")
      case Some(d) if d.contains("//> HTTP-DELETE")  => Some("DELETE")
      case Some(d) if d.contains("//> HTTP-CONNECT") => Some("CONNECT")
      case Some(d) if d.contains("//> HTTP-OPTIONS") => Some("OPTIONS")
      case Some(d) if d.contains("//> HTTP-TRACE")   => Some("TRACE")
      case Some(d) if d.contains("//> HTTP-PATCH")   => Some("PATCH")
      case _                                         => None

case class RoutesExtras(
    httpArgs: Many[ReqParam],
    httpMethod: String
)

case class ReqParam(name: String)
