import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/tests-http4s-server-impl/src/main/generated"
deleteScalaFiles(TargetRoot)

generateReceiver(generatorConfig)
    .includeAvroSerialization
    .includeJsonSerialization
    .generate(TargetRoot, ExportsCatsDep)
