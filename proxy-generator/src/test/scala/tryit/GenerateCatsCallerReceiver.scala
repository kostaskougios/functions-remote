package tryit
import functions.model.GeneratorConfig
import functions.proxygenerator.*
import CatsExporter.*

@main def generateCatsExporterAndImporter(): Unit =
  generateCatsReceiver()
  generateCatsCaller()

@main def generateCatsReceiver(): Unit =
  receiverFor(s"$ProjectRoot/tests-http4s-server-impl/src/main/generated", ExportsCatsDep)

@main def generateCatsCaller(): Unit =
  callerFor(s"$ProjectRoot/tests-http4s-client-impl/src/main/generated", ExportsCatsDep)

object CatsExporter:
  val ProjectRoot                                        = "../end-to-end-tests"
  val ExportsCatsDep                                     = "functions.end-to-end-tests:tests-cats-exports_3:0.1-SNAPSHOT"
  val generatorConfig                                    = GeneratorConfig.withDefaults()
  def receiverFor(targetRoot: String, exportDep: String) =
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig, avroSerialization = true, jsonSerialization = true, http4sRoutes = true)
      .generate(targetRoot, exportDep)

  def callerFor(targetRoot: String, exportDep: String) =
    println(s"---- Importing $exportDep")
    deleteScalaFiles(targetRoot)

    generateCaller(generatorConfig, avroSerialization = true, jsonSerialization = true, http4sClientTransport = true)
      .generate(targetRoot, exportDep)
