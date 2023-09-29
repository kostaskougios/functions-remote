import codegen.model.GeneratorConfig

val ProjectRoot  = os.pwd

val generatorConfig              = GeneratorConfig.withDefaults("../functions-remote-config")

val LsExportsDep = "com.example:ls-exports_3:0.1-SNAPSHOT"
val LsExports    = Seq("ls.LsFunctions")
