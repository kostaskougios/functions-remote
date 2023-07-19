package console.macros.codegenerators

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.model.{Code, CodeFile, EPackage, EType, NewCodeFile}
import org.simplified.templates.ScalaFileTemplate
import org.simplified.templates.ScalaFileTemplate.FileTemplatesSourceLocation
import org.simplified.templates.model.Params
import org.simplified.templates.model.Param

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
  def apply(packages: Seq[EPackage]): Seq[NewCodeFile] =
    packages.flatMap(generate)

  private def generate(`package`: EPackage): Seq[NewCodeFile] =
    `package`.types.map(generate(`package`, _))

  private def generate(`package`: EPackage, `type`: EType): NewCodeFile =
    case class Func(functionN: String, params: Params, resultN: String, caseClass: String)
    case class Vals(packagename: String, imports: String, functionsCaller: String, functionsMethodParams: String, functions: Seq[Func])
    val imports   = `type`.typesInMethods.toSet
    val sn        = namingConventions.className(`type`)
    val mpt       = caseClassNamingConventions.methodParamsTrait(`type`)
    val functions = `type`.methods.map: m =>
      Func(
        m.name,
        Params(m.paramss.flatten.map(ep => Param(ep.name, ep.typeUnqualified))),
        m.returnType.name,
        caseClassNamingConventions.caseClassHolderObject(`type`) + "." + caseClassNamingConventions.methodArgsCaseClassName(`type`, m)
      )
    val code      = scalaFileTemplate(Vals(`package`.name, imports.mkString("\n"), sn, mpt, functions))
    NewCodeFile(
      s"${`package`.toPath}/$sn.scala",
      code
    )

//    val overriddenMethods = `type`.methods.map { m =>
//      s"""
//         |def ${m.name}${m.paramsCodeUnqualified} : ${m.returnType.name} =
//         |  val c  = ${caseClassNamingConventions.caseClassHolderObject(`type`)}.${
//        caseClassNamingConventions.methodArgsCaseClassName(
//          `type`,
//          m
//        )
//      }${m.paramsAsArgs}
//         |  val r1 = $function1Name(c)
//         |  val r2 = $function2Name(r1)
//         |  r2.asInstanceOf[${m.returnType.name}]
//         |""".stripMargin
//    }
//    Seq(
//      CodeFile(
//        s"${`package`.toPath}/$sn.scala",
//        `package`,
//        imports,
//        s"""
//         |class $sn($function1Name: $mpt => $function1ReturnType, $function2Name: $function1ReturnType => Any):
//         |${tabs(1, overriddenMethods).mkString("\n")}
//         |""".stripMargin.trim
//      )
//    )
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
