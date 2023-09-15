package codegen.proxygenerator.codegenerators

import codegen.proxygenerator.codegenerators.model.Config
import codegen.proxygenerator.model.EType
import codegen.proxygenerator.codegenerators
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
