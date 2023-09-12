package console.macros.codegenerators

import console.macros.model.*
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

import scala.language.implicitConversions

object MethodToCaseClassGenerator:

  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = methodParamsTraitName(`type`)

  def apply(
      namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions
  ) = new GenericTypeGenerator(
    namingConventions,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsMethods")
  )
