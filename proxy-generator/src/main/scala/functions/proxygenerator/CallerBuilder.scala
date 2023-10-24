package functions.proxygenerator

import functions.model.{GeneratorConfig, Serializer}
import functions.proxygenerator.codegenerators.*

def generateCaller(generatorConfig: GeneratorConfig): CallerBuilder = new CallerBuilder(
  generatorConfig,
  Seq(
    CallerGenerator(),
    MethodToCaseClassGenerator(),
    FactoryGenerator.caller()
  ),
  Nil,
  false
)

class CallerBuilder(
    generatorConfig: GeneratorConfig,
    generators: Seq[GenericTypeGenerator],
    serializers: Seq[Serializer],
    override protected val isHttp4s: Boolean
) extends AbstractGenerator(generatorConfig, generators, serializers):

  def capabilities(avroSerialization: Boolean = false, jsonSerialization: Boolean = false, http4sClient: Boolean = false): CallerBuilder =
    val (avroGen, avroSer) = if avroSerialization then (List(AvroSerializerGenerator(), AvroFactories.caller()), List(Serializer.Avro)) else (Nil, Nil)
    val (jsonGen, jsonSer) =
      if jsonSerialization then (List(CirceJsonSerializerGenerator(), JsonCirceFactories.caller()), List(Serializer.Json)) else (Nil, Nil)
    new CallerBuilder(generatorConfig, generators ++ avroGen ++ jsonGen, serializers ++ avroSer ++ jsonSer, http4sClient)
