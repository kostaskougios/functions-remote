import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/tests-impl/src/main/generated"
deleteScalaFiles(TargetRoot)

generateReceiver(generatorConfig)
    .includeAvroSerialization
    .includeJsonSerialization
    .generate(TargetRoot, ExportsDep)
