package console.macros

import codegen.proxygenerator.codegenerators.{AvroCaseClassSchemaGenerator, AvroFactories, CallerGenerator, MethodToCaseClassGenerator, ReceiverGenerator}
import codegen.tastyextractor.StructureExtractor

@main def generatorApp() =
  val ProjectRoot = "../example-commands/ls-exports"
  val TargetRoot  = s"$ProjectRoot/src/main/generated"
  val tastyFiles  = List(s"$ProjectRoot/target/scala-3.3.1/classes/ls/LsFunctions.tasty")

  val structureExtractor           = StructureExtractor()
  val callerGenerator              = CallerGenerator()
  val receiverGenerator            = ReceiverGenerator()
  val methodToCaseClassGenerator   = MethodToCaseClassGenerator()
  val avroCaseClassSchemaGenerator = AvroCaseClassSchemaGenerator()
  val callerFactory                = AvroFactories.caller()
  val receiverFactory              = AvroFactories.receiver()
  val packages                     = structureExtractor(tastyFiles)
  val codes                        =
    callerGenerator(packages) ++ methodToCaseClassGenerator(packages) ++ avroCaseClassSchemaGenerator(packages) ++ receiverGenerator(packages) ++
      callerFactory(packages) ++ receiverFactory(packages)
  println(codes.map(c => s"file:${c.file}\n\n${c.code}").mkString("\n-----------\n\n"))
  for c <- codes do c.writeTo(TargetRoot)
