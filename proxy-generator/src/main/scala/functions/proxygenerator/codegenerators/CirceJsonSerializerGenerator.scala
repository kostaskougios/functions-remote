package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object CirceJsonSerializerGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "CirceJsonSerializer"

  def apply(namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions) =
    new GenericTypeGenerator(
      "CirceJsonSerializer",
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsCirceJsonSerializer")
    )
