package console.macros.codegenerators

import console.macros.codegenerators
import console.macros.codegenerators.model.MethodCaseClass
import console.macros.model.*
import mustache.integration.model.{Many, ResourceTemplatesSourceLocation}
import mustache.integration.MustacheTemplate

import scala.language.implicitConversions

class AvroCaseClassSchemaGenerator(namingConventions: GenericTypeGenerator.NamingConventions, template: MustacheTemplate) extends CodeGenerator:
  override def apply(packages: Seq[EPackage]): Seq[Code] = packages.flatMap(apply)
  def apply(`package`: EPackage): Seq[Code]              = `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): Code =
    val mpt         = namingConventions.methodParamsTraitName(`type`)
    val caseClasses = `type`.methods.map(MethodCaseClass.toCaseClass(namingConventions, `package`, `type`, _))

    case class Vals(
        proxypackage: String,
        imports: Many[String],
        functionsMethodAvroSerializer: String,
        methodParams: String,
        caseClasses: Many[MethodCaseClass]
    )
    val n = namingConventions.caseClassHolderObjectName(`type`) + "AvroSerializer"
    Code(
      s"${`package`.toPath}/$n.scala",
      template(Vals(`package`.name, Set(mpt + ".*"), n, mpt, caseClasses))
    )

object AvroCaseClassSchemaGenerator:
  def apply(namingConventions: GenericTypeGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions) =
    new AvroCaseClassSchemaGenerator(
      namingConventions,
      MustacheTemplate(ResourceTemplatesSourceLocation, "proxypackage.FunctionsMethodAvroSerializer")
    )
