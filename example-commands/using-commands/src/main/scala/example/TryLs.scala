package example

import functions.discovery.FunctionsDiscovery
import ls.{LsFunctions, LsFunctionsAvroSerializer}
import ls.model.{LsFile, LsResult}

@main def tryLs() =
  val discovery = FunctionsDiscovery { (cmd, data) =>
    println(s"running $cmd")
    val serializer = new LsFunctionsAvroSerializer
    serializer.lsReturnTypeSerializer(LsResult(Seq(LsFile("/tmp/1"))))
  }
  val functions = discovery.discoverFirstOne[LsFunctions]
  val result    = functions.ls("/tmp")
  println(result)
