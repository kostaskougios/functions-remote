package tryit

import functions.model.GeneratorConfig
import functions.proxygenerator.*
import tryit.KafkaExporter.*

@main def generateKafkaReceiverAndCallerApp(): Unit =
  generateKafkaReceiverApp()
  generateKafkaCallerApp()

@main def generateKafkaReceiverApp(): Unit =
  KafkaExporter.receiverFor(s"$ProjectRoot/tests-kafka-consumer/src/main/functions-remote-generated", ExportsDep)

@main def generateKafkaCallerApp(): Unit =
  KafkaExporter.callerFor(s"$ProjectRoot/tests-kafka-producer/src/main/functions-remote-generated", ExportsDep)

object KafkaExporter:
  val ProjectRoot     = "../end-to-end-tests"
  val ExportsDep      = "functions.end-to-end-tests:tests-kafka-exports_3:0.1-SNAPSHOT"
  val generatorConfig = GeneratorConfig.withDefaults()

  def receiverFor(targetRoot: String, exportDep: String) =
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig, avroSerialization = true, jsonSerialization = true)
      .generate(targetRoot, exportDep)

  def callerFor(targetRoot: String, exportDep: String) =
    println(s"---- Importing $exportDep")
    deleteScalaFiles(targetRoot)

    generateCaller(generatorConfig, avroSerialization = true, jsonSerialization = true)
      .generate(targetRoot, exportDep)
