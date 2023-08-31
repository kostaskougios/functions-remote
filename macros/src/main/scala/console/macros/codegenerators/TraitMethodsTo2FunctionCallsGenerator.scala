package console.macros.codegenerators

import console.macros.codegenerators.TraitMethodsTo2FunctionCallsGenerator.Config
import console.macros.model.*
import mustache.integration.{Many, MustacheTemplate}
import org.simplified.templates.model.{Params, ResourceTemplatesSourceLocation}

import scala.language.implicitConversions

/** Converts a trait A to a class that proxies A's methods. Each proxy converts the method's args to a case class and passes it through 2 functions.
  *
  * Example: function 1 converts the case class to json and function 2 does a rest api call
  */
class TraitMethodsTo2FunctionCallsGenerator(
    config: Config,
    template: MustacheTemplate
) extends CodeGenerator:
  override def apply(packages: Seq[EPackage]): Seq[Code] =
    packages.flatMap(apply)

  def apply(`package`: EPackage): Seq[Code] =
    `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    case class Func(functionN: String, params: Params, resultN: String, caseClass: String)
    case class Vals(
        proxypackage: String,
        imports: Many[String],
        functionsCaller: String,
        function1: String,
        methodParams: String,
        function1ReturnType: String,
        function2: String,
        functions: Seq[Func]
    )
    val imports   = `type`.typesInMethods.toSet
    val sn        = config.traitToSenderNamingConventions.className(`type`)
    val mpt       = config.methodToCaseClassNamingConventions.methodParamsTraitName(`type`)
    val functions = `type`.methods.map: m =>
      Func(
        m.name,
        m.toParams,
        m.returnType.name,
        config.methodToCaseClassNamingConventions.caseClassHolderObjectName(`type`) + "." + config.methodToCaseClassNamingConventions.methodArgsCaseClassName(
          `type`,
          m
        )
      )
    val vals      = Vals(`package`.name, imports, sn, config.function1Name, mpt, config.function1ReturnType, config.function2Name, functions)
    val code      = template(vals)
    Code(
      s"${`package`.toPath}/$sn.scala",
      code
    )

object TraitMethodsTo2FunctionCallsGenerator:
  case class Config(
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions,
      traitToSenderNamingConventions: TraitMethodsTo2FunctionCallsGenerator.NamingConventions = DefaultNamingConventions,
      function1Name: String = "toByteArray",
      function1ReturnType: String = "Array[Byte]",
      function2Name: String = "callFunction"
  )
  trait NamingConventions:
    /** The name of the generated caller class
      * @param `type`
      *   the type where the method belongs to
      * @return
      *   the case class name for the method
      */
    def className(`type`: EType) = s"${`type`.name}Caller"

  object DefaultNamingConventions extends NamingConventions

  def apply(
      config: Config = Config()
  ) = new TraitMethodsTo2FunctionCallsGenerator(
    config,
    MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsCaller")
  )
