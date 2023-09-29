import codegen.proxygenerator.codegenerators.*
import codegen.tastyextractor.StructureExtractor

val TargetRoot = s"$ProjectRoot/ls/src/main/generated"

println(s"Project dir = $ProjectRoot")
println(s"Generated files target dir = $TargetRoot")

val structureExtractor           = StructureExtractor()
val receiverGenerator            = ReceiverGenerator()
val methodToCaseClassGenerator   = MethodToCaseClassGenerator()
val avroCaseClassSchemaGenerator = AvroCaseClassSchemaGenerator()
val receiverFactory              = AvroFactories.receiver()
val packages                     = structureExtractor.forDependency(generatorConfig, LsExportsDep, LsExports)
val codes                        =
  receiverGenerator(packages) ++ methodToCaseClassGenerator(packages) ++ avroCaseClassSchemaGenerator(packages) ++
    receiverFactory(packages)

for c <- codes do c.writeTo(TargetRoot)

println("Done.")
