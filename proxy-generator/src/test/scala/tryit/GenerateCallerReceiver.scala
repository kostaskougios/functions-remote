package tryit

import functions.model.GeneratorConfig
import functions.proxygenerator.*
import tryit.Exporter.*

@main def generateReceiverAndCallerApp(): Unit =
  generateReceiverApp()
  generateCallerApp()

@main def generateReceiverApp(): Unit =
  Exporter.receiverFor(s"$ProjectRoot/tests-impl/src/main/functions-remote-generated", ExportsDep)

@main def generateCallerApp(): Unit =
  Exporter.callerFor(s"$ProjectRoot/using-tests/src/main/functions-remote-generated", ExportsDep)

object Exporter:
  val ProjectRoot     = "../end-to-end-tests"
  val ExportsDep      = "functions.end-to-end-tests:tests-exports_3:0.1-SNAPSHOT"
  val generatorConfig = GeneratorConfig.withDefaults()

  def receiverFor(targetRoot: String, exportDep: String) =
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig, avroSerialization = true, jsonSerialization = true)
      .generate(targetRoot, exportDep)

  def callerFor(targetRoot: String, exportDep: String) =
    println(s"---- Importing $exportDep")
    deleteScalaFiles(targetRoot)

    generateCaller(generatorConfig, avroSerialization = true, jsonSerialization = true, classloaderTransport = true)
      .generate(targetRoot, exportDep)
