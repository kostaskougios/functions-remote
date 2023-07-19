package console.macros.codegenerators

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.codegenerators.MethodToCaseClassGenerator
import console.macros.codegenerators.model.MethodCaseClass
import console.macros.model.*
import org.simplified.templates.ScalaFileTemplate
import org.simplified.templates.model.FileTemplatesSourceLocation

class AvroCaseClassSchemaGenerator(namingConventions: MethodToCaseClassGenerator.NamingConventions, scalaFileTemplate: ScalaFileTemplate):
  def apply(packages: Seq[EPackage]): Seq[NewCodeFile] = packages.flatMap(apply)
  def apply(`package`: EPackage): Seq[NewCodeFile]     = `package`.types.map(apply(`package`, _))

  def apply(`package`: EPackage, `type`: EType): NewCodeFile =
    val mpt         = namingConventions.methodParamsTrait(`type`)
    val caseClasses = `type`.methods.map(MethodCaseClass.toCaseClass(namingConventions, `package`, `type`, _))

    case class Vals(packagename: String, functionsMethodAvroSerializer: String, functionsMethodParams: String, caseClasses: Seq[MethodCaseClass])
    val n = namingConventions.caseClassHolderObject(`type`) + "AvroSerializer"
    NewCodeFile(
      `package`.toPath,
      scalaFileTemplate(Vals(`package`.name, n, mpt, caseClasses))
    )

//    def codeFor(caseClassName: String) =
//      s"""
//        |case value: $caseClassName =>
//        | avroSerialize(AvroOutputStream.data[$caseClassName], value)
//        |""".stripMargin.trim
//    InnerCode(
//      Set.empty,
//      s"""
//       |  private def avroSerialize[A](b : AvroOutputStreamBuilder[A], value: A): Array[Byte] =
//       |    val bos = new ByteArrayOutputStream(4096)
//       |    Using.resource(b.to(bos).build()) { os =>
//       |      os.write(value)
//       |    }
//       |    bos.toByteArray
//       |
//       |  val avroSerializer : PartialFunction[$mpt,Array[Byte]] =
//       |${tabs(2, caseClasses.map(codeFor)).mkString("\n")}
//       |""".stripMargin
//    )

object AvroCaseClassSchemaGenerator:
  def apply(namingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions) =
    new AvroCaseClassSchemaGenerator(
      namingConventions,
      ScalaFileTemplate(FileTemplatesSourceLocation("../proxy-templates/src/main/scala"), "packagename.FunctionsMethodAvroSerializer")
    )
