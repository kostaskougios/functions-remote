package functions.proxygenerator

import functions.model.GeneratorConfig
import functions.proxygenerator.codegenerators.*
import functions.proxygenerator.codegenerators.http4s.RoutesGenerator

def generateReceiver(generatorConfig: GeneratorConfig): ReceiverBuilder = new ReceiverBuilder(
  generatorConfig,
  Seq(
    ReceiverGenerator(),
    MethodToCaseClassGenerator()
  )
)

class ReceiverBuilder(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]) extends AbstractGenerator(generatorConfig, generators):
  def includeAvroSerialization: ReceiverBuilder =
    new ReceiverBuilder(generatorConfig, generators :+ AvroSerializerGenerator() :+ AvroFactories.receiver())
  def includeJsonSerialization: ReceiverBuilder =
    new ReceiverBuilder(generatorConfig, generators :+ CirceJsonSerializerGenerator() :+ JsonCirceFactories.receiver())
  def includeHttp4sRoutes: ReceiverBuilder      =
    new ReceiverBuilder(generatorConfig, generators :+ RoutesGenerator())
