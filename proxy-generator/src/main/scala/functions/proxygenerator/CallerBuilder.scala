package functions.proxygenerator

import functions.model.GeneratorConfig
import functions.proxygenerator.codegenerators.*

def generateCaller(generatorConfig: GeneratorConfig): CallerBuilder = new CallerBuilder(
  generatorConfig,
  Seq(
    CallerGenerator(),
    MethodToCaseClassGenerator()
  )
)

class CallerBuilder(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]) extends AbstractGenerator(generatorConfig, generators):
  def includeAvroSerialization: CallerBuilder =
    new CallerBuilder(generatorConfig, generators :+ AvroSerializerGenerator() :+ AvroFactories.caller())
  def includeJsonSerialization: CallerBuilder =
    new CallerBuilder(generatorConfig, generators :+ CirceJsonSerializerGenerator() :+ JsonCirceFactories.caller())
