package codegen.proxygenerator

import codegen.model.GeneratorConfig
import codegen.proxygenerator.codegenerators.{AvroCaseClassSchemaGenerator, AvroFactories, CallerGenerator, GenericTypeGenerator, MethodToCaseClassGenerator}
import codegen.tastyextractor.StructureExtractor

def generateCaller(generatorConfig: GeneratorConfig): CallerBuilder = new CallerBuilder(
  generatorConfig,
  Seq(
    CallerGenerator(),
    MethodToCaseClassGenerator()
  )
)

class CallerBuilder(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]):
  def includeAvroSerialization: CallerBuilder = new CallerBuilder(generatorConfig, generators :+ AvroCaseClassSchemaGenerator() :+ AvroFactories.caller())
  def generate(targetDir: String, exportDependency: String, exportedClasses: Seq[String]): Unit =
    val structureExtractor = StructureExtractor()
    val packages           = structureExtractor.forDependency(generatorConfig, exportDependency, exportedClasses)
    val codes              = generators.flatMap(_(packages))
    for c <- codes do c.writeTo(targetDir)
