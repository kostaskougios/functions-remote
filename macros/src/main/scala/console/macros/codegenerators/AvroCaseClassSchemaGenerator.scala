package console.macros.codegenerators

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.codegenerators.MethodToCaseClassGenerator
import console.macros.codegenerators.model.MethodCaseClass
import console.macros.model.*
import org.simplified.templates.ScalaFileTemplate
import org.simplified.templates.model.{FileTemplatesSourceLocation, Imports}

class AvroCaseClassSchemaGenerator(namingConventions: MethodToCaseClassGenerator.NamingConventions, scalaFileTemplate: ScalaFileTemplate):
  def apply(packages: Seq[EPackage]): Seq[Code] = packages.flatMap(apply)
  def apply(`package`: EPackage): Seq[Code]     = `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    val mpt         = namingConventions.methodParamsTrait(`type`)
    val caseClasses = `type`.methods.map(MethodCaseClass.toCaseClass(namingConventions, `package`, `type`, _))

    case class Vals(
        packagename: String,
        imports: Imports,
        functionsMethodAvroSerializer: String,
        functionsMethodParams: String,
        caseClasses: Seq[MethodCaseClass]
    )
    val n = namingConventions.caseClassHolderObject(`type`) + "AvroSerializer"
    Code(
      s"${`package`.toPath}/$n.scala",
      scalaFileTemplate(Vals(`package`.name, Imports(Set(mpt + ".*")), n, mpt, caseClasses))
    )

object AvroCaseClassSchemaGenerator:
  def apply(namingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions) =
    new AvroCaseClassSchemaGenerator(
      namingConventions,
      ScalaFileTemplate(FileTemplatesSourceLocation("../proxy-templates/src/main/scala"), "packagename.FunctionsMethodAvroSerializer")
    )
