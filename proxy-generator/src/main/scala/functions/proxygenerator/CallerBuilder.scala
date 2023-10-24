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
  Nil
)

class CallerBuilder(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator], serializers: Seq[Serializer])
    extends AbstractGenerator(generatorConfig, generators, serializers):
  def includeAvroSerialization: CallerBuilder =
    new CallerBuilder(generatorConfig, generators :+ AvroSerializerGenerator() :+ AvroFactories.caller(), serializers :+ Serializer.Avro)
  def includeJsonSerialization: CallerBuilder =
    new CallerBuilder(generatorConfig, generators :+ CirceJsonSerializerGenerator() :+ JsonCirceFactories.caller(), serializers :+ Serializer.Json)
