package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object AvroSerializerGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "AvroSerializer"

  def apply(namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions) =
    new GenericTypeGenerator(
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsAvroSerializer")
    )
