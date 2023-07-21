package console.macros

import console.macros.codegenerators.{AvroCaseClassSchemaGenerator, GeneratorBuilder, MethodToCaseClassGenerator, TraitMethodsTo2FunctionCallsGenerator}

@main def generatorApp() =
//  val structureExtractor      = StructureExtractor()
//  val caseClassGenerator      = MethodToCaseClassGenerator()
//  val callerGenerator         = TraitMethodsTo2FunctionCallsGenerator()
//  val avroSerializerGenerator = AvroCaseClassSchemaGenerator()
  val ProjectRoot = "../example-commands/ls-exports"
  val TargetRoot  = s"$ProjectRoot/src/main/generated"
  val tastyFiles  = List(s"$ProjectRoot/target/scala-3.3.0/classes/ls/LsFunctions.tasty")
//  val packages                = structureExtractor(tastyFiles)
//  val codes                   = callerGenerator(packages) ++ caseClassGenerator(packages) ++ avroSerializerGenerator(packages)

  val codes = GeneratorBuilder().generateCode(tastyFiles)

  println(codes.map(c => s"file:${c.file}\n\n${c.code}").mkString("\n-----------\n\n"))
  for c <- codes do c.writeTo(TargetRoot)
