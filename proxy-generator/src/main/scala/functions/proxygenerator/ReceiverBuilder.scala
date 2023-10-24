package functions.proxygenerator

import functions.model.{GeneratorConfig, Serializer}
import functions.proxygenerator.codegenerators.*
import functions.proxygenerator.codegenerators.http4s.RoutesGenerator

def generateReceiver(generatorConfig: GeneratorConfig): ReceiverBuilder = new ReceiverBuilder(
  generatorConfig,
  Seq(
    ReceiverGenerator(),
    MethodToCaseClassGenerator(),
    EntryPointFactoryGenerator.receiver()
  ),
  Nil,
  false
)

class ReceiverBuilder(
    generatorConfig: GeneratorConfig,
    generators: Seq[GenericTypeGenerator],
    serializers: Seq[Serializer],
    override protected val isHttp4s: Boolean
) extends AbstractGenerator(generatorConfig, generators, serializers):

  def capabilities(avroSerialization: Boolean = false, jsonSerialization: Boolean = false, http4sRoutes: Boolean = false): ReceiverBuilder =
    val (avroGen, avroSer) = if avroSerialization then (List(AvroSerializerGenerator(), AvroFactories.receiver()), List(Serializer.Avro)) else (Nil, Nil)
    val (jsonGen, jsonSer) =
      if jsonSerialization then (List(CirceJsonSerializerGenerator(), JsonCirceFactories.receiver()), List(Serializer.Json)) else (Nil, Nil)

    val http4sRoutesGen = if http4sRoutes then List(RoutesGenerator()) else Nil
    new ReceiverBuilder(generatorConfig, generators ++ avroGen ++ jsonGen ++ http4sRoutesGen, serializers ++ avroSer ++ jsonSer, http4sRoutes)
