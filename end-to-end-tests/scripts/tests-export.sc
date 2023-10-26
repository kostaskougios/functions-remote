import functions.proxygenerator.*

def exportFor(targetRoot:String,exportDep:String)=
    println(s"---- Exporting $exportDep")
    deleteScalaFiles(targetRoot)

    generateReceiver(generatorConfig,avroSerialization = true, jsonSerialization = true)
        .generate(targetRoot, exportDep)

exportFor(s"$ProjectRoot/tests-impl/src/main/generated",ExportsDep)
