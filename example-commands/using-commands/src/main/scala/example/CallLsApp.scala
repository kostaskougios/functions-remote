import commands.ls.LsFunctionsCallerFactory
import functions.model.RuntimeConfig

/** Run me with -Dfunctions.debug to see debugging information. Set env var functions-remote.config.dir to the functions-remote-config directory so that
  * functions jars can be located.
  */
def builder =
  val runtimeConfig = RuntimeConfig.withDefaults() // set env var functions-remote.config.dir to your functions-remote-config directory
  LsFunctionsCallerFactory.newClassloaderBuilder(runtimeConfig)

@main
def callLsFunctionViaAvroSerialization() =
  val lsFunctions = builder.newAvroLsFunctions
  // Now we will call the function which will avro-serialize the params via LsFunctionsMethods.Ls() case
  // class and then load the lsFunctions jars via an isolated classloader. The classloader isolates the
  // classpath of this caller with the classpath of the lsFunctions jars, this way we can have incompatible
  // libraries for each.
  // Next the exported.Exported impl in lsFunctions is invoked which routes to our actual LsFunctionsImpl class.
  val result      = lsFunctions.ls("/tmp")
  println(result)

@main
def callLsFunctionViaJsonSerialization() =
  val lsFunctions = builder.newJsonLsFunctions
  val result      = lsFunctions.ls("/tmp")
  println(result)
