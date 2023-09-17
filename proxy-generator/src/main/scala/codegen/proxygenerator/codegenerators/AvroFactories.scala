package codegen.proxygenerator.codegenerators

import codegen.proxygenerator.codegenerators.model.Config
import codegen.proxygenerator.codegenerators
import codegen.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object AvroFactories:
  object DefaultCallerNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "CallerAvroSerializedFactory"

  object DefaultReceiverNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "ReceiverAvroSerializedFactory"

  def caller(namingConventions: GenericTypeGenerator.NamingConventions = DefaultCallerNamingConventions, config: Config = Config()): GenericTypeGenerator =
    new GenericTypeGenerator(
      namingConventions,
      config,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsCallerAvroSerializedFactory")
    )

  def receiver(namingConventions: GenericTypeGenerator.NamingConventions = DefaultReceiverNamingConventions, config: Config = Config()): GenericTypeGenerator =
    new GenericTypeGenerator(
      namingConventions,
      config,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsReceiverAvroSerializedFactory")
    )
