package console.macros.codegenerators

import console.macros.StructureExtractor
import console.macros.codegenerators.TraitMethodsTo2FunctionCallsGenerator.Config
import console.macros.model.Code

object CallerProxyBuilder:

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

  def apply(
      methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions,
      traitToSenderNamingConventions: TraitMethodsTo2FunctionCallsGenerator.NamingConventions = TraitMethodsTo2FunctionCallsGenerator.DefaultNamingConventions,
      function1Name: String = "toByteArray",
      function1ReturnType: String = "Array[Byte]",
      function2Name: String = "callFunction"
  ): Builder =
    val structureExtractor = StructureExtractor()
    val caseClassGenerator = MethodToCaseClassGenerator(methodToCaseClassNamingConventions)
    val callerGenerator    = TraitMethodsTo2FunctionCallsGenerator(
      Config(
        methodToCaseClassNamingConventions,
        traitToSenderNamingConventions,
        function1Name,
        function1ReturnType,
        function2Name
      )
    )
    Builder(structureExtractor, methodToCaseClassNamingConventions, Seq(caseClassGenerator, callerGenerator))
