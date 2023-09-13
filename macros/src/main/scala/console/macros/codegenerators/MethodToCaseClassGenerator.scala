package console.macros.codegenerators

import console.macros.codegenerators.GenericTypeGenerator.Config
import console.macros.model.*
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

import scala.language.implicitConversions

object MethodToCaseClassGenerator:

  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = methodParamsTraitName(`type`)

  def apply(
      namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions,
      config: Config = Config()
  ) = new GenericTypeGenerator(
    namingConventions,
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsMethods")
  )
