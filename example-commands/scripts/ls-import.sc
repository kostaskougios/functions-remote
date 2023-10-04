import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/using-commands/src/main/generated"
deleteScalaFiles(TargetRoot)

generateCaller(generatorConfig)
    .includeAvroSerialization
    .includeJsonSerialization
    .generate(TargetRoot, LsExportsDep)
