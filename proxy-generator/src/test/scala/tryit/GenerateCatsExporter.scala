package tryit
import functions.model.GeneratorConfig
import functions.proxygenerator.*
import CatsExporter.*

@main def generateCatsExporterAndImporter(): Unit =
  generateCatsExporter()
  generateCatsImporter()

@main def generateCatsExporter(): Unit =
  exportFor(s"$ProjectRoot/tests-http4s-server-impl/src/main/generated", ExportsCatsDep)

@main def generateCatsImporter(): Unit =
  importsFor(s"$ProjectRoot/tests-http4s-client-impl/src/main/generated", ExportsCatsDep)

object CatsExporter:
  val ProjectRoot                                      = "../end-to-end-tests"
  val ExportsCatsDep                                   = "functions.end-to-end-tests:tests-cats-exports_3:0.1-SNAPSHOT"
  val generatorConfig                                  = GeneratorConfig.withDefaults(s"$ProjectRoot/config")
  def exportFor(targetRoot: String, exportDep: String) =
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig, avroSerialization = true, jsonSerialization = true, http4sRoutes = true)
      .generate(targetRoot, exportDep)

  def importsFor(targetRoot: String, exportDep: String) =
    println(s"---- Importing $exportDep")
    deleteScalaFiles(targetRoot)

    generateCaller(generatorConfig, avroSerialization = true, jsonSerialization = true, http4sClient = true)
      .generate(targetRoot, exportDep)
