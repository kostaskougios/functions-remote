package console.macros.codegenerators

import console.macros.codegenerators
import console.macros.codegenerators.model.Config
import console.macros.model.EType
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
