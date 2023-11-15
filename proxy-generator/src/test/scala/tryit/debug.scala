package tryit

import functions.model.GeneratorConfig
import tryit.Exporter.generatorConfig
import functions.proxygenerator.*

@main def debug() =
  val targetRoot      = "/tmp/debug-generator"
  val exportDep       = "com.example:cats-ls-exports_3:0.1-SNAPSHOT"
  val generatorConfig = GeneratorConfig.withDefaults()
  deleteScalaFiles(targetRoot)
  generateCaller(generatorConfig, avroSerialization = true, jsonSerialization = true, classloaderTransport = true)
    .generate(targetRoot, exportDep)
