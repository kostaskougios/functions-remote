package tryit
import functions.model.GeneratorConfig
import functions.proxygenerator.*
import org.apache.commons.io.FileUtils

import java.io.File

val ProjectRoot     = "../end-to-end-tests"
val ExportsCatsDep  = "functions.end-to-end-tests:tests-cats-exports_3:0.1-SNAPSHOT"
val generatorConfig = GeneratorConfig.withDefaults(s"$ProjectRoot/config")

def deleteScalaFiles(dir: String) =
  val f = new File(dir)
  println(s"Deleting scala files from ${f.getAbsolutePath}")
  FileUtils.deleteDirectory(f)

@main def generateCatsExporter() =
  exportFor(s"$ProjectRoot/tests-cats-impl/src/main/generated", ExportsCatsDep)

def exportFor(targetRoot: String, exportDep: String) =
  println(s"---- Exporting $exportDep")
  deleteScalaFiles(targetRoot)

  generateReceiver(generatorConfig).includeAvroSerialization.includeJsonSerialization
    .generate(targetRoot, exportDep)
