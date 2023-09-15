package console.macros.codegenerators

import console.macros.codegenerators.GenericTypeGenerator.NamingConventions
import console.macros.codegenerators.model.Config
import console.macros.model.*
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object ReceiverGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}Receiver"

  def apply(
      namingConventions: NamingConventions = DefaultNamingConventions,
      config: Config = Config()
  ) = new GenericTypeGenerator(
    namingConventions,
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsReceiver")
  )
