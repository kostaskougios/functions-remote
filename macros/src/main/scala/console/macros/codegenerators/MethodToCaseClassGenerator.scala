package console.macros.codegenerators

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.codegenerators.model.MethodCaseClass
import console.macros.model.*
import org.simplified.templates.ScalaFileTemplate
import org.simplified.templates.model.{FileTemplatesSourceLocation, Imports, Params}

class MethodToCaseClassGenerator(
    namingConventions: MethodToCaseClassGenerator.NamingConventions,
    scalaFileTemplate: ScalaFileTemplate
):
  def apply(packages: Seq[EPackage]): Seq[Code]                = packages.flatMap(p => apply(p, p.types))
  def apply(`package`: EPackage, types: Seq[EType]): Seq[Code] = types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    val caseClasses = `type`.methods.map(MethodCaseClass.toCaseClass(namingConventions, `package`, `type`, _))
    val n           = namingConventions.caseClassHolderObject(`type`)
    val imports     = caseClasses.flatMap(_.imports).toSet

    case class Vals(proxypackage: String, imports: Imports, functionsMethodParams: String, caseClasses: Seq[MethodCaseClass])
    val code = scalaFileTemplate(Vals(`package`.name, Imports(imports), n, caseClasses))
    Code(
      s"${`package`.toPath}/$n.scala",
      code
    )

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
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = DefaultNamingConventions
  ) = new MethodToCaseClassGenerator(
    methodToCaseClassNamingConventions,
    ScalaFileTemplate(FileTemplatesSourceLocation("../proxy-templates/src/main/scala"), "proxypackage.FunctionsMethodParams")
  )
