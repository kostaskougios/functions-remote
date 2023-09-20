//> using dependency org.kkougios::proxy-generator:0.1-SNAPSHOT
//> using dependency com.lihaoyi::os-lib:0.9.1

import codegen.proxygenerator.codegenerators.*
import codegen.tastyextractor.StructureExtractor

val ProjectRoot = os.pwd 
val TargetRoot  = s"$ProjectRoot/ls/src/main/generated"
val tastyFiles  = List(s"$ProjectRoot/ls-exports/target/scala-3.3.1/classes/ls/LsFunctions.tasty")

println(s"Project dir = $ProjectRoot")
println(s"Generated files target dir = $TargetRoot")
println(s"Tasty files to import = ${tastyFiles.mkString(", ")}")

val structureExtractor           = StructureExtractor()
val receiverGenerator            = ReceiverGenerator()
val methodToCaseClassGenerator   = MethodToCaseClassGenerator()
val avroCaseClassSchemaGenerator = AvroCaseClassSchemaGenerator()
val receiverFactory                = AvroFactories.receiver()
val packages                     = structureExtractor(tastyFiles)
val codes                        =
    receiverGenerator(packages) ++ methodToCaseClassGenerator(packages) ++ avroCaseClassSchemaGenerator(packages) ++ 
        receiverFactory(packages)

for c <- codes do c.writeTo(TargetRoot)

println("Done.")