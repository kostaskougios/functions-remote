package console.macros.codegenerators

import console.macros.codegenerators.ReceiverGenerator.Config
import console.macros.model.*
import mustache.integration.MustacheTemplate
import mustache.integration.model.{Many, ResourceTemplatesSourceLocation}

import scala.language.implicitConversions

/** Converts a trait A to a class that proxies A's methods. Each proxy converts the method's args to a case class and passes it through 2 functions.
  *
  * Example: function 1 converts the case class to json and function 2 does a rest api call
  */
class ReceiverGenerator(
    config: Config,
    template: MustacheTemplate
) extends CodeGenerator:
  override def apply(packages: Seq[EPackage]): Seq[Code] =
    packages.flatMap(apply)

  def apply(`package`: EPackage): Seq[Code] =
    `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    case class Func(functionN: String, params: String, paramsCall: String, resultN: String, caseClass: String, caseClassName: String)
    case class Vals(
        proxypackage: String,
        imports: Many[String],
        functionsReceiver: String,
        methodParams: String,
        functions: Many[Func]
    )
    val imports   = `type`.typesInMethods.toSet
    val sn        = config.traitToSenderNamingConventions.className(`type`)
    val mpt       = config.methodToCaseClassNamingConventions.methodParamsTraitName(`type`)
    val functions = `type`.methods.map: m =>
      val params        = m.toParams
      val caseClassName = config.methodToCaseClassNamingConventions.methodArgsCaseClassName(
        `type`,
        m
      )
      Func(
        m.name,
        params.toMethodDeclArguments,
        params.toMethodCallArguments,
        m.returnType.name,
        config.methodToCaseClassNamingConventions.caseClassHolderObjectName(`type`) + "." + caseClassName,
        caseClassName
      )

    val vals = Vals(`package`.name, imports, sn, mpt, functions)
    val code = template(vals)
    Code(
      s"${`package`.toPath}/$sn.scala",
      code
    )

object ReceiverGenerator:
  case class Config(
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions,
      traitToSenderNamingConventions: ReceiverGenerator.NamingConventions = DefaultNamingConventions
  )
  trait NamingConventions:
    /** The name of the generated caller class
      * @param `type`
      *   the type where the method belongs to
      * @return
      *   the case class name for the method
      */
    def className(`type`: EType) = s"${`type`.name}Receiver"

  object DefaultNamingConventions extends NamingConventions

  def apply(
      config: Config = Config()
  ) = new ReceiverGenerator(
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsReceiver")
  )
