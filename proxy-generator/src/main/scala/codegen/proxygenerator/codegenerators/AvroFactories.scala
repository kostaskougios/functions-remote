package codegen.proxygenerator.codegenerators

import codegen.proxygenerator.codegenerators.model.Config
import codegen.proxygenerator.model.EType
import codegen.proxygenerator.codegenerators
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object AvroFactories:
  object DefaultCallerNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "AvroSerializedCallerFactory"

  def caller(namingConventions: GenericTypeGenerator.NamingConventions = DefaultCallerNamingConventions, config: Config = Config()) =
    new GenericTypeGenerator(
      namingConventions,
      config,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsAvroSerializedCallerFactory")
    )
