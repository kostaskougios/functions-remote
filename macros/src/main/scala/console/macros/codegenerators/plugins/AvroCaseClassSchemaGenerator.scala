package console.macros.codegenerators.plugins

import console.macros.codegenerators.CodeFormatter.tabs
import console.macros.codegenerators.MethodToCaseClassGenerator
import console.macros.model.{EMethod, EPackage, EType, InnerCode}

class AvroCaseClassSchemaGenerator(namingConventions: MethodToCaseClassGenerator.NamingConventions)
    extends MethodToCaseClassGenerator.CaseClassGenerationPlugin:
  override def extraCode(`package`: EPackage, `type`: EType) =
    val mpt                            = namingConventions.methodParamsTrait(`type`)
    val caseClasses                    = `type`.methods.map(namingConventions.methodArgsCaseClassName(`type`, _))
    def codeFor(caseClassName: String) =
      s"""
        |case value: $caseClassName =>
        | avroSerialize(AvroOutputStream.data[$caseClassName], value)
        |""".stripMargin.trim
    InnerCode(
      Set.empty,
      s"""
       |  private def avroSerialize[A](b : AvroOutputStreamBuilder[A], value: A): Array[Byte] =
       |    val bos = new ByteArrayOutputStream(4096)
       |    Using.resource(b.to(bos).build()) { os =>
       |      os.write(value)
       |    }
       |    bos.toByteArray
       |
       |  val avroSerializer : PartialFunction[$mpt,Array[Byte]] =
       |${tabs(2, caseClasses.map(codeFor)).mkString("\n")}
       |""".stripMargin
    )

object AvroCaseClassSchemaGenerator:
  def apply(namingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions) =
    new AvroCaseClassSchemaGenerator(namingConventions)
