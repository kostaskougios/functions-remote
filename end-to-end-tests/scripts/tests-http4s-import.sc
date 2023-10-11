import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/tests-http4s-client-impl/src/main/generated"
deleteScalaFiles(TargetRoot)

generateCaller(generatorConfig)
    .includeAvroSerialization
    .includeJsonSerialization
    .generate(TargetRoot, ExportsDep)
