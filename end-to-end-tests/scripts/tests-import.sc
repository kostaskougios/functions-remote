import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/using-tests/src/main/generated"
deleteScalaFiles(TargetRoot)

generateCaller(generatorConfig)
    .includeAvroSerialization
    .includeJsonSerialization
    .generate(TargetRoot, ExportsDep)
