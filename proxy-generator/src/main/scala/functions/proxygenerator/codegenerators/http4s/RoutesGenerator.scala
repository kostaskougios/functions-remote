package functions.proxygenerator.codegenerators.http4s

import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
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
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.http4s.Routes")
  )
