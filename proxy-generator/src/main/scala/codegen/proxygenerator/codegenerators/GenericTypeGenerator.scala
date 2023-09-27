package codegen.proxygenerator.codegenerators

import codegen.proxygenerator.codegenerators.model.{Code, Func, Vals}
import codegen.tastyextractor.model.{EMethod, EPackage, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.Many

import scala.language.implicitConversions

class GenericTypeGenerator(
    namingConventions: GenericTypeGenerator.NamingConventions,
    template: MustacheTemplate
):
  def apply(packages: Seq[EPackage]): Seq[Code] =
    packages.flatMap(apply)

  def apply(`package`: EPackage): Seq[Code] =
    `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    val imports   = `type`.typesInMethods.toSet
    val className = namingConventions.className(`type`)
    val mpt       = namingConventions.methodParamsTraitName(`type`)
    val functions = model.Func(`type`, namingConventions)

    val vals = Vals(`type`, `package`.name, imports, className, mpt, functions)
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
