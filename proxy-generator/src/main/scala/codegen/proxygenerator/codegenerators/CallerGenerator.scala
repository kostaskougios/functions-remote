package codegen.proxygenerator.codegenerators

import codegen.proxygenerator.codegenerators.model.Config
import GenericTypeGenerator.NamingConventions
import codegen.tastyextractor.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

import scala.language.implicitConversions

object CallerGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    def className(`type`: EType) = s"${`type`.name}Caller"

  def apply(
      namingConventions: NamingConventions = DefaultNamingConventions,
      config: Config = Config()
  ) = new GenericTypeGenerator(
    namingConventions,
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsCaller")
  )