import codegen.proxygenerator.*

val TargetRoot = s"$ProjectRoot/using-commands/src/main/generated"
deleteScalaFiles(TargetRoot)

println(s"Project dir = $ProjectRoot")
println(s"Generated files target dir = $TargetRoot")
println(s"config dir = ${generatorConfig.root.getAbsolutePath()}")

generateCaller(generatorConfig).includeAvroSerialization.generate(TargetRoot, LsExportsDep, LsExports)
println("Done.")
