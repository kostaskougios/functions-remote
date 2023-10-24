package functions.proxygenerator

import functions.model.{GeneratorConfig, Serializer}
import functions.proxygenerator.codegenerators.*
import functions.proxygenerator.codegenerators.http4s.RoutesGenerator

def generateReceiver(generatorConfig: GeneratorConfig): ReceiverBuilder = new ReceiverBuilder(
  generatorConfig,
  Seq(
    ReceiverGenerator(),
    MethodToCaseClassGenerator()
  ),
  Nil
)

class ReceiverBuilder(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator], serializers: Seq[Serializer])
    extends AbstractGenerator(generatorConfig, generators, serializers):
  def includeAvroSerialization: ReceiverBuilder =
    new ReceiverBuilder(generatorConfig, generators :+ AvroSerializerGenerator() :+ AvroFactories.receiver(), serializers :+ Serializer.Avro)
  def includeJsonSerialization: ReceiverBuilder =
    new ReceiverBuilder(generatorConfig, generators :+ CirceJsonSerializerGenerator() :+ JsonCirceFactories.receiver(), serializers :+ Serializer.Json)
  def includeHttp4sRoutes: ReceiverBuilder      =
    new ReceiverBuilder(generatorConfig, generators :+ RoutesGenerator(), serializers)
