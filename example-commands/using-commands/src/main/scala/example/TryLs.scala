package example

import commands.ls.LsFunctions
import functions.discovery.FunctionsDiscovery

@main def tryLs() =
  val discovery = FunctionsDiscovery()
  val functions = discovery.discover[LsFunctions]
  for (f <- functions) do
    println(s"Using ${f.serializer} with transport ${f.transport}")
    val result = f.function.ls("/tmp")
    println(result)
