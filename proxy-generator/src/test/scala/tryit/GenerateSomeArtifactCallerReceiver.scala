package tryit

import functions.model.GeneratorConfig
import functions.proxygenerator.*
import tryit.SomeArtifactExporter.*

@main def generateSomeArtifactReceiverAndCallerApp(): Unit =
  generateSomeArtifactReceiverApp()
  generateSomeArtifactCallerApp()

@main def generateSomeArtifactReceiverApp(): Unit =
  SomeArtifactExporter.receiverFor(s"$ProjectRoot/functions-remote-generated", ExportsDep)

@main def generateSomeArtifactCallerApp(): Unit =
  SomeArtifactExporter.callerFor(s"$ProjectRoot/functions-remote-generated", ExportsDep)

object SomeArtifactExporter:
  val ProjectRoot     = "/tmp/SomeArtifactExporter"
  val ExportsDep      = "org.terminal21:terminal21-ui-std-exports_3:0.1-SNAPSHOT"
  val generatorConfig = GeneratorConfig.withDefaults()

  def receiverFor(targetRoot: String, exportDep: String) =
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig, avroSerialization = true, jsonSerialization = true, helidonRoutes = true)
      .generate(targetRoot, exportDep)

  def callerFor(targetRoot: String, exportDep: String) =
    println(s"---- Importing $exportDep")
    deleteScalaFiles(targetRoot)

    generateCaller(generatorConfig, avroSerialization = true, jsonSerialization = true, classloaderTransport = true, helidonClient = true)
      .generate(targetRoot, exportDep)
