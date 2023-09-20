import codegen.proxygenerator.codegenerators.*
import codegen.tastyextractor.StructureExtractor

val TargetRoot  = s"$ProjectRoot/ls/src/main/generated"

println(s"Project dir = $ProjectRoot")
println(s"Generated files target dir = $TargetRoot")
println(s"Tasty files to import = ${LsTastyFiles.mkString(", ")}")

val structureExtractor           = StructureExtractor()
val receiverGenerator            = ReceiverGenerator()
val methodToCaseClassGenerator   = MethodToCaseClassGenerator()
val avroCaseClassSchemaGenerator = AvroCaseClassSchemaGenerator()
val receiverFactory                = AvroFactories.receiver()
val packages                     = structureExtractor(LsTastyFiles)
val codes                        =
    receiverGenerator(packages) ++ methodToCaseClassGenerator(packages) ++ avroCaseClassSchemaGenerator(packages) ++ 
        receiverFactory(packages)

for c <- codes do c.writeTo(TargetRoot)

println("Done.")