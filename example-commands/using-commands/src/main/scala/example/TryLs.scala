package example

import functions.discovery.FunctionsDiscovery
import ls.{LsFunctions, LsFunctionsCallerAvroSerializedFactory}

@main def tryLs() =
  val discovery = FunctionsDiscovery { (cmd, data) =>
    println(s"running $cmd")
    Array.emptyByteArray
  }
  val functions = discovery.discoverFirstOne[LsFunctions]
  functions.ls("/tmp")
