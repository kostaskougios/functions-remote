package console.macros.codegenerators

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.model.{Code, CodeFile, EMethod, EPackage, EType, InnerCode}

class MethodToCaseClassGenerator(
    namingConventions: MethodToCaseClassGenerator.NamingConventions,
    caseClassGenerationPlugins: Seq[MethodToCaseClassGenerator.CaseClassGenerationPlugin]
):
  def apply(packages: Seq[EPackage]): Seq[CodeFile]                = packages.flatMap(p => apply(p, p.types))
  def apply(`package`: EPackage, types: Seq[EType]): Seq[CodeFile] = types.flatMap(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Seq[CodeFile] =
    val caseClasses = `type`.methods.map(toCaseClass(`package`, `type`, _))
    val n           = namingConventions.caseClassHolderObject(`type`)
    val mpt         = namingConventions.methodParamsTrait(`type`)
    val extraCode   = caseClassGenerationPlugins.map(_.extraCode(`package`, `type`))
    val imports     = caseClasses.flatMap(_.imports) ++ extraCode.flatMap(_.imports)
    Seq(
      CodeFile(
        s"${`package`.toPath}/$n.scala",
        `package`,
        imports.toSet,
        s"""
       |trait $mpt
       |
       |object $n:
       |${tabs(1, caseClasses.map(_.main)).mkString("\n")}
       |${extraCode.map(_.main).mkString("\n")}
       |""".stripMargin
      )
    )

  /** @param `package`
    *   the package where the method is declared
    * @param `type`
    *   the type where the method is declared
    * @param method
    *   the method itself to be converted to case class
    * @return
    */
  private def toCaseClass(`package`: EPackage, `type`: EType, method: EMethod): InnerCode =
    val params  = method.paramsCodeUnqualified
    val n       = namingConventions.methodArgsCaseClassName(`type`, method)
    val imports = `type`.typesInMethods.toSet

    caseClassGenerationPlugins.foldLeft(
      InnerCode(
        imports,
        s"""
         |case class $n$params extends ${namingConventions.methodParamsTrait(`type`)}
         |""".stripMargin.trim
      )
    ) { (c, p) => c + p.extraCodeForMethod(n, `package`, `type`, method) }

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
  ) = new MethodToCaseClassGenerator(methodToCaseClassNamingConventions, caseClassGenerationPlugins)

  trait CaseClassGenerationPlugin:
    def extraCode(`package`: EPackage, `type`: EType): InnerCode                                                     = InnerCode.Empty
    def extraCodeForMethod(caseClassName: String, `package`: EPackage, `type`: EType, forMethod: EMethod): InnerCode = InnerCode.Empty
