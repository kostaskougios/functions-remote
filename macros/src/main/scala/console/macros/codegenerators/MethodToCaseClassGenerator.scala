package console.macros.codegenerators

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.model.{Code, CodeFile, EMethod, EPackage, EType, InnerCode, NewCodeFile}
import org.simplified.templates.ScalaFileTemplate
import org.simplified.templates.model.{FileTemplatesSourceLocation, Params}

class MethodToCaseClassGenerator(
    namingConventions: MethodToCaseClassGenerator.NamingConventions,
    caseClassGenerationPlugins: Seq[MethodToCaseClassGenerator.CaseClassGenerationPlugin],
    scalaFileTemplate: ScalaFileTemplate
):
  def apply(packages: Seq[EPackage]): Seq[NewCodeFile]                = packages.flatMap(p => apply(p, p.types))
  def apply(`package`: EPackage, types: Seq[EType]): Seq[NewCodeFile] = types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): NewCodeFile =
    val caseClasses = `type`.methods.map(toCaseClass(`package`, `type`, _))
    val n           = namingConventions.caseClassHolderObject(`type`)
    val mpt         = namingConventions.methodParamsTrait(`type`)
    val extraCode   = caseClassGenerationPlugins.map(_.extraCode(`package`, `type`))
    val imports     = caseClasses.flatMap(_.imports) ++ extraCode.flatMap(_.imports)

    case class Vals(packagename: String, functionsMethodParams: String, caseClasses: Seq[CaseClass])
    val code = scalaFileTemplate(Vals(`package`.name, n, caseClasses))
    NewCodeFile(
      s"${`package`.toPath}/$n.scala",
      code
    )

  private case class CaseClass(imports: Set[String], caseClass: String, params: Params)

  /** @param `package`
    *   the package where the method is declared
    * @param `type`
    *   the type where the method is declared
    * @param method
    *   the method itself to be converted to case class
    * @return
    */
  private def toCaseClass(`package`: EPackage, `type`: EType, method: EMethod): CaseClass =
    val params  = method.toParams
    val n       = namingConventions.methodArgsCaseClassName(`type`, method)
    val imports = `type`.typesInMethods.toSet

    CaseClass(imports, n, params)

object MethodToCaseClassGenerator:
  trait NamingConventions:
    /** @param `type`
      *   the type where the method belongs to
      * @param method
      *   the method
      * @return
      *   the case class name for the method
      */
    def methodArgsCaseClassName(`type`: EType, method: EMethod): String = method.name.capitalize
    def methodParamsTrait(`type`: EType): String                        = s"${`type`.name}MethodParams"
    def caseClassHolderObject(`type`: EType): String                    = methodParamsTrait(`type`)

  object DefaultNamingConventions extends NamingConventions

  def apply(
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = DefaultNamingConventions,
      caseClassGenerationPlugins: Seq[MethodToCaseClassGenerator.CaseClassGenerationPlugin] = Nil
  ) = new MethodToCaseClassGenerator(
    methodToCaseClassNamingConventions,
    caseClassGenerationPlugins,
    ScalaFileTemplate(FileTemplatesSourceLocation("../proxy-templates/src/main/scala"), "packagename.FunctionsMethodParams")
  )

  trait CaseClassGenerationPlugin:
    def extraCode(`package`: EPackage, `type`: EType): InnerCode                                                     = InnerCode.Empty
    def extraCodeForMethod(caseClassName: String, `package`: EPackage, `type`: EType, forMethod: EMethod): InnerCode = InnerCode.Empty
