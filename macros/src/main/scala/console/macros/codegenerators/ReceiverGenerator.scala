package console.macros.codegenerators

import console.macros.codegenerators.GenericTypeGenerator.Config
import console.macros.model.*
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object ReceiverGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}Receiver"

  def apply(
      config: Config = Config(namingConventions = DefaultNamingConventions)
  ) = new GenericTypeGenerator(
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsReceiver")
  )
