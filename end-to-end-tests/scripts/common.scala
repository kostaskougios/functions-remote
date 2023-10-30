import functions.model.GeneratorConfig
import java.io.File
import org.apache.commons.io.FileUtils

val ProjectRoot = os.pwd

val generatorConfig = GeneratorConfig.withDefaults()

val ExportsDep = "functions.end-to-end-tests:tests-exports_3:0.1-SNAPSHOT"
val ExportsCatsDep = "functions.end-to-end-tests:tests-cats-exports_3:0.1-SNAPSHOT"

def deleteScalaFiles(dir: String) = 
    println(s"Deleting scala files from $dir")
    FileUtils.deleteDirectory(new File(dir))
