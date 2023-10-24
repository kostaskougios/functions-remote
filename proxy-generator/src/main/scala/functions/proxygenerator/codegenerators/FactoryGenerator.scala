package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object FactoryGenerator:
  object DefaultCallerNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}CallerFactory"

  def caller(
      namingConventions: NamingConventions = DefaultCallerNamingConventions
  ) = new GenericTypeGenerator(
    namingConventions,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.CallerFactory")
  )
