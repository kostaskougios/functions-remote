package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object AvroFactories:
  object DefaultCallerNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "CallerAvroSerializedFactory"

  object DefaultReceiverNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "ReceiverAvroSerializedFactory"

  def caller(namingConventions: GenericTypeGenerator.NamingConventions = DefaultCallerNamingConventions): GenericTypeGenerator =
    new GenericTypeGenerator(
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsCallerAvroSerializedFactory")
    )

  def receiver(namingConventions: GenericTypeGenerator.NamingConventions = DefaultReceiverNamingConventions): GenericTypeGenerator =
    new GenericTypeGenerator(
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsReceiverAvroSerializedFactory")
    )
