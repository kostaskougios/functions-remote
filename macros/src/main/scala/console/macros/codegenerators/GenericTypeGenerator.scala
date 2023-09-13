package console.macros.codegenerators

import console.macros.codegenerators.model.Func
import console.macros.model.{Code, EMethod, EPackage, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.Many

import scala.language.implicitConversions

class GenericTypeGenerator(
    namingConventions: GenericTypeGenerator.NamingConventions,
    config: GenericTypeGenerator.Config,
    template: MustacheTemplate
) extends CodeGenerator:
  override def apply(packages: Seq[EPackage]): Seq[Code] =
    packages.flatMap(apply)

  def apply(`package`: EPackage): Seq[Code] =
    `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    case class Vals(
        config: GenericTypeGenerator.Config,
        exportedType: EType,
        proxypackage: String,
        imports: Many[String],
        className: String,
        methodParams: String,
        functions: Many[Func]
    )
    val imports   = `type`.typesInMethods.toSet
    val sn        = namingConventions.className(`type`)
    val mpt       = namingConventions.methodParamsTraitName(`type`)
    val functions = Func(`type`, namingConventions)

    val vals = Vals(config, `type`, `package`.name, imports, sn, mpt, functions)
    val code = template(vals)
    Code(
      s"${`package`.toPath}/$sn.scala",
      code
    )

object GenericTypeGenerator:
  case class Config(apiVersion: String = "v1")
  trait NamingConventions:
    /** The name of the generated caller class
      *
      * @param `type`
      *   the type where the method belongs to
      * @return
      *   the case class name for the method
      */
    def className(`type`: EType): String

    /** The name of the generated case class for a method args.
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
