package console.macros

import console.macros.codegenerators.{AvroCaseClassSchemaGenerator, AvroFactories, CallerGenerator, MethodToCaseClassGenerator, ReceiverGenerator}

@main def generatorApp() =
  val ProjectRoot = "../example-commands/ls-exports"
  val TargetRoot  = s"$ProjectRoot/src/main/generated"
  val tastyFiles  = List(s"$ProjectRoot/target/scala-3.3.1/classes/ls/LsFunctions.tasty")

  val structureExtractor           = StructureExtractor()
  val callerGenerator              = CallerGenerator()
  val receiverGenerator            = ReceiverGenerator()
  val methodToCaseClassGenerator   = MethodToCaseClassGenerator()
  val avroCaseClassSchemaGenerator = AvroCaseClassSchemaGenerator()
  val factories                    = AvroFactories.caller()
  val packages                     = structureExtractor(tastyFiles)
  val codes                        =
    callerGenerator(packages) ++ methodToCaseClassGenerator(packages) ++ avroCaseClassSchemaGenerator(packages) ++ receiverGenerator(packages) ++
      factories(packages)
  println(codes.map(c => s"file:${c.file}\n\n${c.code}").mkString("\n-----------\n\n"))
  for c <- codes do c.writeTo(TargetRoot)
