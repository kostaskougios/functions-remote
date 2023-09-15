package console.macros.codegenerators

import console.macros.codegenerators
import console.macros.codegenerators.model.Config
import console.macros.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object AvroCaseClassSchemaGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "AvroSerializer"

  def apply(namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions, config: Config = Config()) =
    new GenericTypeGenerator(
      namingConventions,
      config,
      MustacheTemplate(ResourceTemplatesSourceLocation,  "proxypackage.FunctionsAvroSerializer")
    )
