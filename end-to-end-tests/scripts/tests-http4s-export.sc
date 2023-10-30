import functions.proxygenerator.*

val TargetRoot = s"$ProjectRoot/tests-http4s-server-impl/src/main/generated"

generateReceiver(generatorConfig,avroSerialization = true, jsonSerialization = true, http4sRoutes = true)
    .generate(TargetRoot, ExportsCatsDep)
