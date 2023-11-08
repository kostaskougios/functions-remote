package functions.proxygenerator.codegenerators

import GenericTypeGenerator.NamingConventions
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.{ResourceTemplatesSourceLocation, Template}

object FunctionsReceiverGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}Receiver"

  def apply(
      namingConventions: NamingConventions = DefaultNamingConventions
  ) = new GenericTypeGenerator(
    "Receiver",
    namingConventions,
    MustacheTemplate(
      ResourceTemplatesSourceLocation,
      "proxypackage.FunctionsReceiver",
      Seq(
        Template("CatsFunctionImpl", ResourceTemplatesSourceLocation, "proxypackage.functionsreceiver.CatsFunctionImpl"),
        Template("NoFrameworkImpl", ResourceTemplatesSourceLocation, "proxypackage.functionsreceiver.NoFrameworkImpl")
      )
    )
  )
