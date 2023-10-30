import functions.proxygenerator.*

generateReceiver(generatorConfig,avroSerialization = true, jsonSerialization = true)
    .generate(s"$ProjectRoot/tests-impl/src/main/generated",ExportsDep)
