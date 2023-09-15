package console.macros.codegenerators

import console.macros.codegenerators
import console.macros.codegenerators.GenericTypeGenerator.Config
import console.macros.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object AvroFactories:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = caseClassHolderObjectName(`type`) + "AvroSerializedFactory"

  def apply(namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions, config: Config = Config()) =
    new GenericTypeGenerator(
      namingConventions,
      config,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsMethodAvroSerializedFactory")
    )
