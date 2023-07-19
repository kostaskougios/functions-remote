package console.macros

import console.macros.codegenerators.plugins.AvroCaseClassSchemaGenerator
import console.macros.codegenerators.{MethodToCaseClassGenerator, TraitMethodsTo2FunctionCallsGenerator}

@main def generatorApp() =
  val structureExtractor = StructureExtractor()
  val caseClassGenerator = MethodToCaseClassGenerator(caseClassGenerationPlugins = Seq(AvroCaseClassSchemaGenerator()))
  val callerGenerator    = TraitMethodsTo2FunctionCallsGenerator()
  val ProjectRoot        = "../example-commands/ls-exports"
  val TargetRoot         = s"$ProjectRoot/src/main/generated"
  val tastyFiles         = List(s"$ProjectRoot/target/scala-3.3.0/classes/ls/LsFunctions.tasty")
  val packages           = structureExtractor(tastyFiles)
  val codes              = callerGenerator(packages) ++ caseClassGenerator(packages)

  println(callerGenerator(packages).map(_.code).mkString("\n"))
//  println(codes.map(c => s"file:${c.file}\n\n${c.toCode}").mkString("\n-----------\n\n"))
//  for c <- codes do c.writeTo(TargetRoot)
