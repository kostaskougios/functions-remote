import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/using-tests/src/main/generated"
deleteScalaFiles(TargetRoot)

generateCaller(generatorConfig,avroSerialization = true, jsonSerialization = true,classloaderTransport = true)
    .generate(TargetRoot, ExportsDep)
