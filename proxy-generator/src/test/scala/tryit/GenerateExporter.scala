package tryit

import functions.model.GeneratorConfig
import functions.proxygenerator.*
import tryit.Exporter.*

@main def generateReceiverAndCallerApp(): Unit =
  generateReceiverApp()
  generateCallerApp()

@main def generateReceiverApp(): Unit =
  Exporter.exportFor(s"$ProjectRoot/tests-impl/src/main/generated", ExportsDep)

@main def generateCallerApp(): Unit =
  Exporter.importsFor(s"$ProjectRoot/using-tests/src/main/generated", ExportsDep)

object Exporter:
  val ProjectRoot     = "../end-to-end-tests"
  val ExportsDep      = "functions.end-to-end-tests:tests-exports_3:0.1-SNAPSHOT"
  val generatorConfig = GeneratorConfig.withDefaults(s"$ProjectRoot/config")

  def exportFor(targetRoot: String, exportDep: String) =
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig, avroSerialization = true, jsonSerialization = true)
      .generate(targetRoot, exportDep)

  def importsFor(targetRoot: String, exportDep: String) =
    println(s"---- Importing $exportDep")
    deleteScalaFiles(targetRoot)

    generateCaller(generatorConfig, avroSerialization = true, jsonSerialization = true)
      .generate(targetRoot, exportDep)
