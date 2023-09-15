package codegen.proxygenerator.codegenerators

import codegen.proxygenerator.codegenerators.model.Config
import codegen.proxygenerator.model.EType
import GenericTypeGenerator.NamingConventions
import codegen.proxygenerator.model.*
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
