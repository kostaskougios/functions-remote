package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object JsonCirceFactories:
  object DefaultCallerNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "CallerJsonSerializedFactory"

  object DefaultReceiverNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = `type`.name + "ReceiverJsonSerializedFactory"

  def caller(namingConventions: GenericTypeGenerator.NamingConventions = DefaultCallerNamingConventions): GenericTypeGenerator =
    new GenericTypeGenerator(
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsCallerCirceJsonSerializedFactory")
    )

  def receiver(namingConventions: GenericTypeGenerator.NamingConventions = DefaultReceiverNamingConventions): GenericTypeGenerator =
    new GenericTypeGenerator(
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsReceiverCirceJsonSerializedFactory")
    )
