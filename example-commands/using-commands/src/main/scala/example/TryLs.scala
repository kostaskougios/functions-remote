package example

import commands.ls.LsFunctions
import functions.discovery.FunctionsDiscovery

@main def tryLs() =
  val discovery = FunctionsDiscovery()
  val functions = discovery.discover[LsFunctions]
  for (functionDetails <- functions) do
    val function = functionDetails.function
    println(s"Using ${functionDetails.serializer} with transport ${functionDetails.transport} and function $function")
    val result   = function.ls("/tmp")
    println(result)
    val sz       = function.fileSize("/tmp/file1")
    println(sz)
