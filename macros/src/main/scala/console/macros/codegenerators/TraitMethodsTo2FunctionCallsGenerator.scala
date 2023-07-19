package console.macros.codegenerators

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.model.*
import org.simplified.templates.ScalaFileTemplate
import org.simplified.templates.model.{FileTemplatesSourceLocation, Imports, Param, Params}

/** Converts a trait A to a class that proxies A's methods. Each proxy converts the method's args to a case class and passes it through 2 functions.
  *
  * Example: function 1 converts the case class to json and function 2 does a rest api call
  */
class TraitMethodsTo2FunctionCallsGenerator(
    namingConventions: TraitMethodsTo2FunctionCallsGenerator.NamingConventions,
    caseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions,
    function1Name: String,
    function1ReturnType: String,
    function2Name: String,
    scalaFileTemplate: ScalaFileTemplate
):
  def apply(packages: Seq[EPackage]): Seq[Code] =
    packages.flatMap(generate)

  private def generate(`package`: EPackage): Seq[Code] =
    `package`.types.map(generate(`package`, _))

  private def generate(`package`: EPackage, `type`: EType): Code =
    case class Func(functionN: String, params: Params, resultN: String, caseClass: String)
    case class Vals(
        packagename: String,
        imports: Imports,
        functionsCaller: String,
        function1: String,
        functionsMethodParams: String,
        function1ReturnType: String,
        function2: String,
        functions: Seq[Func]
    )
    val imports   = `type`.typesInMethods.toSet
    val sn        = namingConventions.className(`type`)
    val mpt       = caseClassNamingConventions.methodParamsTrait(`type`)
    val functions = `type`.methods.map: m =>
      Func(
        m.name,
        m.toParams,
        m.returnType.name,
        caseClassNamingConventions.caseClassHolderObject(`type`) + "." + caseClassNamingConventions.methodArgsCaseClassName(`type`, m)
      )
    val code      = scalaFileTemplate(Vals(`package`.name, Imports(imports), sn, function1Name, mpt, function1ReturnType, function2Name, functions))
    Code(
      s"${`package`.toPath}/$sn.scala",
      code
    )

object TraitMethodsTo2FunctionCallsGenerator:
  trait NamingConventions:
    /** @param `type`
      *   the type where the method belongs to
      * @return
      *   the case class name for the method
      */
    def className(`type`: EType) = s"${`type`.name}Caller"

  object DefaultNamingConventions extends NamingConventions

  def apply(
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions,
      traitToSenderNamingConventions: TraitMethodsTo2FunctionCallsGenerator.NamingConventions = DefaultNamingConventions,
      function1Name: String = "toByteArray",
      function1ReturnType: String = "Array[Byte]",
      function2Name: String = "callFunction"
  ) = new TraitMethodsTo2FunctionCallsGenerator(
    traitToSenderNamingConventions,
    methodToCaseClassNamingConventions,
    function1Name,
    function1ReturnType,
    function2Name,
    ScalaFileTemplate(FileTemplatesSourceLocation("../proxy-templates/src/main/scala"), "packagename.FunctionsCaller")
  )
