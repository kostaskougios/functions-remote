package example

import ls.LsFunctionsCallerAvroSerializedFactory

@main def tryLs() =
  val functions = LsFunctionsCallerAvroSerializedFactory.createCaller { (cmd, data) =>
    println(s"running $cmd")
    Array.emptyByteArray
  }
  functions.ls("/tmp")
