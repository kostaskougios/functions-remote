package console.macros.codegenerators

import console.macros.StructureExtractor
import console.macros.codegenerators.Generator.Config
import console.macros.model.Code

object CallerProxy:

  case class Builder(
      structureExtractor: StructureExtractor,
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions,
      generators: Seq[CodeGenerator]
  ):
    def withAvroBinarySerialization: Builder =
      copy(generators = generators :+ AvroCaseClassSchemaGenerator(methodToCaseClassNamingConventions))

    def generateCode(tastyFiles: Seq[String]): Seq[Code] =
      val packages = structureExtractor(tastyFiles.toList)
      generators.flatMap(_(packages))

  def builder(
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions,
      callerNamingConventions: Generator.NamingConventions = CallerGenerator.DefaultNamingConventions
  ): Builder =
    val structureExtractor = StructureExtractor()
    val caseClassGenerator = MethodToCaseClassGenerator()
    val callerGenerator    = CallerGenerator(
      Config(
        methodToCaseClassNamingConventions,
        callerNamingConventions
      )
    )
    Builder(structureExtractor, methodToCaseClassNamingConventions, Seq(caseClassGenerator, callerGenerator))
