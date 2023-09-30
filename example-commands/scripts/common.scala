import codegen.model.GeneratorConfig
import java.io.File

val ProjectRoot = os.pwd

val generatorConfig = GeneratorConfig.withDefaults(s"$ProjectRoot/functions-remote-config")

val LsExportsDep = "com.example:ls-exports_3:0.1-SNAPSHOT"
val LsExports    = Seq("ls.LsFunctions")

def deleteScalaFiles(dir: String) = 
    println(s"Deleting scala files from $dir")
    new File(dir).listFiles().filter(_.getName().endsWith(".scala")).foreach(_.delete())
