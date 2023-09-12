package console.macros.codegenerators

import console.macros.codegenerators.Generator.Config
import console.macros.codegenerators.model.MethodCaseClass
import console.macros.codegenerators.model.MethodCaseClass.toCaseClass
import console.macros.model.*
import mustache.integration.model.{Many, ResourceTemplatesSourceLocation}
import mustache.integration.MustacheTemplate

import scala.language.implicitConversions

object MethodToCaseClassGenerator:
  trait NamingConventions:
    /** The name of the generated case class for a method args.
      * @param `type`
      *   the type where the method belongs to
      * @param method
      *   the method
      * @return
      *   the case class name for the method
      */
    def methodArgsCaseClassName(`type`: EType, method: EMethod): String = method.name.capitalize

    /** The name of a trait that will be the super class of all generated case classes
      */
    def methodParamsTraitName(`type`: EType): String = s"${`type`.name}Methods"

    /** The name of the object that will hold all case classes
      */
    def caseClassHolderObjectName(`type`: EType): String = methodParamsTraitName(`type`)

  object DefaultNamingConventions extends NamingConventions

  object DefaultClassNamingConventions extends Generator.NamingConventions:
    override def className(`type`: EType) = s"${`type`.name}Methods"

  def apply(
      config: Config = Config(namingConventions = DefaultClassNamingConventions)
  ) = new Generator(
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsMethodParams")
  )
