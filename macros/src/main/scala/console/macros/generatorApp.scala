package console.macros

import console.macros.codegenerators.{CallerProxy, ReceiverGenerator}

@main def generatorApp() =
  val ProjectRoot = "../example-commands/ls-exports"
  val TargetRoot  = s"$ProjectRoot/src/main/generated"
  val tastyFiles  = List(s"$ProjectRoot/target/scala-3.3.0/classes/ls/LsFunctions.tasty")

  val codes =
    CallerProxy.builder().withAvroBinarySerialization.generateCode(tastyFiles) ++ ReceiverGenerator.apply().apply(StructureExtractor.apply().apply(tastyFiles))
  println(codes.map(c => s"file:${c.file}\n\n${c.code}").mkString("\n-----------\n\n"))
  for c <- codes do c.writeTo(TargetRoot)
