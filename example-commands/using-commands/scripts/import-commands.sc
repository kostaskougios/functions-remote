//> using dependency org.kkougios::proxy-generator:1.0
//> using dependency com.lihaoyi::os-lib:0.9.1

import codegen.proxygenerator.codegenerators.*
import codegen.tastyextractor.StructureExtractor

val ProjectRoot=os.pwd / os.up
val TargetRoot  = s"$ProjectRoot/using-commands/src/main/generated"
val tastyFiles  = List(s"$ProjectRoot/ls-exports/target/scala-3.3.1/classes/ls/LsFunctions.tasty")

println(s"Project dir = $ProjectRoot")
println(s"Generated files target dir = $TargetRoot")
println(s"Tasty files to import = ${tastyFiles.mkString(", ")}")

val structureExtractor           = StructureExtractor()
val callerGenerator              = CallerGenerator()
val methodToCaseClassGenerator   = MethodToCaseClassGenerator()
val avroCaseClassSchemaGenerator = AvroCaseClassSchemaGenerator()
val callerFactory                = AvroFactories.caller()
val packages                     = structureExtractor(tastyFiles)
val codes                        =
    callerGenerator(packages) ++ methodToCaseClassGenerator(packages) ++ avroCaseClassSchemaGenerator(packages) ++ 
        callerFactory(packages)

for c <- codes do c.writeTo(TargetRoot)

println("Done.")