package functions.proxygenerator.codegenerators.helidon

import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
import functions.proxygenerator.codegenerators.model.Func
import functions.tastyextractor.model.{EMethod, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.{Many, ResourceTemplatesSourceLocation}
import scala.language.implicitConversions

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
        httpArgs.params.nonEmpty,
        httpArgs.params.map(p => ReqParam(p.name, p.`type`)),
        httpMethod(method).getOrElse("put")
      )
  )

  def httpMethod(method: EMethod): Option[String] =
    method.scalaDocs match
      case Some(d) if d.contains("//> HTTP-GET")     => Some("get")
      case Some(d) if d.contains("//> HTTP-POST")    => Some("post")
      case Some(d) if d.contains("//> HTTP-PUT")     => Some("put")
      case Some(d) if d.contains("//> HTTP-HEAD")    => Some("head")
      case Some(d) if d.contains("//> HTTP-DELETE")  => Some("delete")
      case Some(d) if d.contains("//> HTTP-CONNECT") => Some("connect")
      case Some(d) if d.contains("//> HTTP-OPTIONS") => Some("options")
      case Some(d) if d.contains("//> HTTP-TRACE")   => Some("trace")
      case Some(d) if d.contains("//> HTTP-PATCH")   => Some("patch")
      case _                                         => None

case class RoutesExtras(
    hasHttpArgs: Boolean,
    httpArgs: Many[ReqParam],
    httpMethod: String
)

case class ReqParam(name: String, tpe: String)
