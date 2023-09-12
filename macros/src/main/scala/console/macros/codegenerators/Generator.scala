package console.macros.codegenerators

import console.macros.codegenerators.model.Func
import console.macros.model.{Code, EPackage, EType}
import mustache.integration.MustacheTemplate
import mustache.integration.model.Many
import scala.language.implicitConversions

class Generator(
    config: Generator.Config,
    template: MustacheTemplate
) extends CodeGenerator:
  override def apply(packages: Seq[EPackage]): Seq[Code] =
    packages.flatMap(apply)

  def apply(`package`: EPackage): Seq[Code] =
    `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    case class Vals(
        proxypackage: String,
        imports: Many[String],
        functionsProxy: String,
        methodParams: String,
        functions: Many[Func]
    )
    val imports   = `type`.typesInMethods.toSet
    val sn        = config.namingConventions.className(`type`)
    val mpt       = config.methodToCaseClassNamingConventions.methodParamsTraitName(`type`)
    val functions = Func(`type`, config.methodToCaseClassNamingConventions)

    val vals = Vals(`package`.name, imports, sn, mpt, functions)
    val code = template(vals)
    Code(
      s"${`package`.toPath}/$sn.scala",
      code
    )

object Generator:
  trait NamingConventions:
    /** The name of the generated caller class
      *
      * @param `type`
      *   the type where the method belongs to
      * @return
      *   the case class name for the method
      */
    def className(`type`: EType): String

  case class Config(
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions,
      namingConventions: NamingConventions
  )
