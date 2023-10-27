import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/ls/src/main/generated"
deleteScalaFiles(TargetRoot)

generateReceiver(generatorConfig,avroSerialization = true,jsonSerialization = true)
    .generate(TargetRoot, LsExportsDep)
