package example

import commands.ls.LsFunctions
import functions.discovery.FunctionsDiscovery

@main def tryLs() =
  val discovery = FunctionsDiscovery()
  val functions = discovery.discoverFirstOne[LsFunctions]
  val result    = functions.ls("/tmp")
  println(result)
