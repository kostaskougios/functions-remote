// Common config and methods for caller & receiver scripts

import functions.model.GeneratorConfig
import java.io.File
import org.apache.commons.io.FileUtils

val ProjectRoot = os.pwd

val generatorConfig = GeneratorConfig.withDefaults(s"$ProjectRoot/functions-remote-config")

val LsExportsDep = "com.example:ls-exports_3:0.1-SNAPSHOT"

def deleteScalaFiles(dir: String) = 
    println(s"Deleting scala files from $dir")
    FileUtils.deleteDirectory(new File(dir))
