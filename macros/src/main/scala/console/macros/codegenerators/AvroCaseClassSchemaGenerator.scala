package console.macros.codegenerators

import console.macros.codegenerators
import console.macros.model.EType
import mustache.integration.MustacheTemplate
import mustache.integration.model.ResourceTemplatesSourceLocation

object AvroCaseClassSchemaGenerator:
  object DefaultNamingConventions extends GenericTypeGenerator.NamingConventions:
    override def className(`type`: EType) = caseClassHolderObjectName(`type`) + "AvroSerializer"

  def apply(namingConventions: GenericTypeGenerator.NamingConventions = DefaultNamingConventions) =
    new GenericTypeGenerator(
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsMethodAvroSerializer")
    )
