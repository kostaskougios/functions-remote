import codegen.proxygenerator.codegenerators.*
import codegen.tastyextractor.StructureExtractor

val TargetRoot = s"$ProjectRoot/using-commands/src/main/generated"

println(s"Project dir = $ProjectRoot")
println(s"Generated files target dir = $TargetRoot")
println(s"config dir = ${generatorConfig.root.getAbsolutePath()}")

val structureExtractor           = StructureExtractor()
val callerGenerator              = CallerGenerator()
val methodToCaseClassGenerator   = MethodToCaseClassGenerator()
val avroCaseClassSchemaGenerator = AvroCaseClassSchemaGenerator()
val callerFactory                = AvroFactories.caller()
val packages                     = structureExtractor.forDependency(generatorConfig, LsExportsDep, LsExports)
val codes                        =
  callerGenerator(packages) ++ methodToCaseClassGenerator(packages) ++ avroCaseClassSchemaGenerator(packages) ++
    callerFactory(packages)

for c <- codes do c.writeTo(TargetRoot)

println("Done.")
