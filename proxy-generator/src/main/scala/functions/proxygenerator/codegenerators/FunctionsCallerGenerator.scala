package functions.proxygenerator.codegenerators

import GenericTypeGenerator.NamingConventions
import functions.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.{ResourceTemplatesSourceLocation, Template}

import scala.language.implicitConversions

object FunctionsCallerGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}Caller"

  def apply(
      namingConventions: NamingConventions = DefaultNamingConventions
  ) = new GenericTypeGenerator(
    "Caller",
    namingConventions,
    MustacheTemplate(
      ResourceTemplatesSourceLocation,
      "proxypackage.FunctionsCaller",
      Seq(
        Template("CatsFunctionImpl", ResourceTemplatesSourceLocation, "proxypackage.functionscaller.CatsFunctionImpl"),
        Template("NoFrameworkImpl", ResourceTemplatesSourceLocation, "proxypackage.functionscaller.NoFrameworkImpl")
      )
    )
  )
