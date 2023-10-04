import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/ls/src/main/generated"
deleteScalaFiles(TargetRoot)

generateReceiver(generatorConfig)
    .includeAvroSerialization
    .includeJsonSerialization
    .generate(TargetRoot, LsExportsDep)
