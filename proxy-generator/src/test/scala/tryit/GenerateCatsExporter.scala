package tryit
import functions.model.GeneratorConfig
import functions.proxygenerator.*
import org.apache.commons.io.FileUtils

import java.io.File

val ProjectRoot     = "../end-to-end-tests"
val ExportsCatsDep  = "functions.end-to-end-tests:tests-cats-exports_3:0.1-SNAPSHOT"
val generatorConfig = GeneratorConfig.withDefaults(s"$ProjectRoot/config")

@main def generateCatsExporter(): Unit =
  exportFor(s"$ProjectRoot/tests-http4s-server-impl/src/main/generated", ExportsCatsDep)

@main def generateCatsImporter(): Unit =
  importsFor(s"$ProjectRoot/tests-http4s-client-impl/src/main/generated", ExportsCatsDep)

def deleteScalaFiles(dir: String) =
  val f = new File(dir)
  println(s"Deleting scala files from ${f.getAbsolutePath}")
  FileUtils.deleteDirectory(f)

def exportFor(targetRoot: String, exportDep: String) =
  println(s"---- Exporting $exportDep")
  deleteScalaFiles(targetRoot)

  generateReceiver(generatorConfig).includeAvroSerialization.includeJsonSerialization
    .generate(targetRoot, exportDep)

def importsFor(targetRoot: String, exportDep: String) =
  println(s"---- Importing $exportDep")
  deleteScalaFiles(targetRoot)

  generateCaller(generatorConfig).includeAvroSerialization.includeJsonSerialization
    .generate(targetRoot, exportDep)
