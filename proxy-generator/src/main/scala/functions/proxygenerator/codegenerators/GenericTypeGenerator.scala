package functions.proxygenerator.codegenerators

import functions.proxygenerator.codegenerators.model.{Code, Func, Vals}
import functions.tastyextractor.model.{EMethod, EPackage, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.{GeneratorFactories, Many}

import scala.language.implicitConversions

class GenericTypeGenerator(
    namingConventions: GenericTypeGenerator.NamingConventions,
    template: MustacheTemplate
):
  def apply(packages: Seq[EPackage], generatorFactories: GeneratorFactories): Seq[Code] =
    packages.flatMap(apply(_, generatorFactories))

  def apply(`package`: EPackage, generatorFactories: GeneratorFactories): Seq[Code] =
    `package`.types.map(apply(`package`, _, generatorFactories))

  def apply(`package`: EPackage, `type`: EType, generatorFactories: GeneratorFactories): Code =
    val imports          = `package`.imports.map(_.fullName)
    val frameworkImports = `type`.framework.toSeq.flatMap(_.imports).toSet
    val className        = namingConventions.className(`type`)
    val mpt              = namingConventions.methodParamsTraitName(`type`)
    val functions        = model.Func(`type`, namingConventions)

    val vals = Vals(
      `type`,
      `package`.name,
      imports,
      frameworkImports,
      imports ++ frameworkImports,
      className,
      mpt,
      functions,
      generatorFactories
    )
    val code = template(vals)
    Code(
      s"${`package`.toPath}/$className.scala",
      code
    )

object GenericTypeGenerator:
  trait NamingConventions:
    /** The name of the generated class
      *
      * @param `type`
      *   the type where the method belongs to
      * @return
      *   the name that the generated class should have
      */
    def className(`type`: EType): String

    /** The name of the generated case class for a method.
      *
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
