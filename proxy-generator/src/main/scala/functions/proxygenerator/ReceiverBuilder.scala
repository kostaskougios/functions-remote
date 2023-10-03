package functions.proxygenerator

import functions.model.GeneratorConfig
import functions.proxygenerator.codegenerators.*

def generateReceiver(generatorConfig: GeneratorConfig): ReceiverBuilder = new ReceiverBuilder(
  generatorConfig,
  Seq(
    ReceiverGenerator(),
    MethodToCaseClassGenerator()
  )
)

class ReceiverBuilder(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]) extends AbstractGenerator(generatorConfig, generators):
  def includeAvroSerialization: CallerBuilder = new CallerBuilder(generatorConfig, generators :+ AvroCaseClassSchemaGenerator() :+ AvroFactories.receiver())
