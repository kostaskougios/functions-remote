import codegen.proxygenerator.*

val TargetRoot = s"$ProjectRoot/using-commands/src/main/generated"
deleteScalaFiles(TargetRoot)

generateCaller(generatorConfig).includeAvroSerialization.generate(TargetRoot, LsExportsDep, LsExports)
println("Done.")
