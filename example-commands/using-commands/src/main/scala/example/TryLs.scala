package example

import functions.discovery.FunctionsDiscovery
import ls.LsFunctions

@main def tryLs() =
  val discovery = FunctionsDiscovery()
  val functions = discovery.discoverFirstOne[LsFunctions]
  val result    = functions.ls("/tmp")
  println(result)
