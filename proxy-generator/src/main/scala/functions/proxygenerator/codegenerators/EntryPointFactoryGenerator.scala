package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.{ResourceTemplatesSourceLocation, Template}

/** Generates factories code that serve as entry points of using the generated classes
  */
object EntryPointFactoryGenerator:
  object DefaultCallerNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}CallerFactory"

  def caller(
      namingConventions: NamingConventions = DefaultCallerNamingConventions
  ) = new GenericTypeGenerator(
    "CallerEntryPointFactory",
    namingConventions,
    MustacheTemplate(
      ResourceTemplatesSourceLocation,
      "proxypackage.CallerFactory",
      Seq(
        Template("Generic", ResourceTemplatesSourceLocation, "proxypackage.callerfactory.Generic"),
        Template("IsolatedClassLoader", ResourceTemplatesSourceLocation, "proxypackage.callerfactory.IsolatedClassLoader")
      )
    )
  )

  object DefaultReceiverNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}ReceiverFactory"

  def receiver(
      namingConventions: NamingConventions = DefaultReceiverNamingConventions
  ) = new GenericTypeGenerator(
    "ReceiverEntryPointFactory",
    namingConventions,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.ReceiverFactory")
  )
