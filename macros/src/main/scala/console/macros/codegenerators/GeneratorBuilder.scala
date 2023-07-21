package console.macros.codegenerators

import console.macros.StructureExtractor
import console.macros.codegenerators.TraitMethodsTo2FunctionCallsGenerator.Config
import console.macros.model.Code

case class GeneratorBuilder(
    methodToCaseClassNamingConventions: MethodToCaseClassGenerator.NamingConventions = MethodToCaseClassGenerator.DefaultNamingConventions,
    traitToSenderNamingConventions: TraitMethodsTo2FunctionCallsGenerator.NamingConventions = TraitMethodsTo2FunctionCallsGenerator.DefaultNamingConventions,
    function1Name: String = "toByteArray",
    function1ReturnType: String = "Array[Byte]",
    function2Name: String = "callFunction"
):
  private val structureExtractor = StructureExtractor()

  private val caseClassGenerator = MethodToCaseClassGenerator(methodToCaseClassNamingConventions = methodToCaseClassNamingConventions)
  private val callerGenerator    = TraitMethodsTo2FunctionCallsGenerator(
    Config(
      methodToCaseClassNamingConventions,
      traitToSenderNamingConventions,
      function1Name,
      function1ReturnType,
      function2Name
    )
  )

  def generateCode(tastyFiles: Seq[String]): Seq[Code] =
    val packages = structureExtractor(tastyFiles.toList)
    callerGenerator(packages) ++ caseClassGenerator(packages)
