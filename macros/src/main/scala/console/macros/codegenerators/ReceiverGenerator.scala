package console.macros.codegenerators

import console.macros.codegenerators.GenericTypeTransformerGenerator.Config
import console.macros.model.*
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object ReceiverGenerator:
  object DefaultNamingConventions extends GenericTypeTransformerGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}Receiver"

  def apply(
      config: Config = Config(namingConventions = DefaultNamingConventions)
  ) = new GenericTypeTransformerGenerator(
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsReceiver")
  )
