package tryit

import functions.model.GeneratorConfig
import functions.proxygenerator.*
import tryit.HelidonExporter.*

@main def generateHelidonReceiverAndCallerApp(): Unit =
  generateHelidonReceiverApp()
  generateHelidonCallerApp()

@main def generateHelidonReceiverApp(): Unit =
  HelidonExporter.receiverFor(s"$ProjectRoot/tests-helidon-server/src/main/functions-remote-generated", ExportsDep)

@main def generateHelidonCallerApp(): Unit =
  HelidonExporter.callerFor(s"$ProjectRoot/tests-helidon-client/src/main/functions-remote-generated", ExportsDep)

object HelidonExporter:
  val ProjectRoot     = "../end-to-end-tests"
  val ExportsDep      = "functions.end-to-end-tests:tests-helidon-exports_3:0.1-SNAPSHOT"
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
