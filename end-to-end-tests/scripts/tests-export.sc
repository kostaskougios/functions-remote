import functions.proxygenerator.*

def exportFor(targetRoot:String,exportDep:String)=
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig)
        .includeAvroSerialization
        .includeJsonSerialization
        .generate(targetRoot, exportDep)

exportFor(s"$ProjectRoot/tests-impl/src/main/generated",ExportsDep)
exportFor(s"$ProjectRoot/tests-cats-impl/src/main/generated",ExportsCatsDep)