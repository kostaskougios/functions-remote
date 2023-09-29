package codegen.proxygenerator

import codegen.model.GeneratorConfig
import codegen.proxygenerator.codegenerators.*

def generateCaller(generatorConfig: GeneratorConfig): CallerBuilder = new CallerBuilder(
  generatorConfig,
  Seq(
    CallerGenerator(),
    MethodToCaseClassGenerator()
  )
)

class CallerBuilder(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]) extends AbstractGenerator(generatorConfig, generators):
  def includeAvroSerialization: CallerBuilder = new CallerBuilder(generatorConfig, generators :+ AvroCaseClassSchemaGenerator() :+ AvroFactories.caller())
