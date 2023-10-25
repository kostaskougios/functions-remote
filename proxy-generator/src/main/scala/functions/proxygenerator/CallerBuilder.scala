package functions.proxygenerator

import functions.model.{GeneratorConfig, Serializer}
import functions.proxygenerator.codegenerators.*

def generateCaller(
    generatorConfig: GeneratorConfig,
    avroSerialization: Boolean = false,
    jsonSerialization: Boolean = false,
    http4sClient: Boolean = false
): Generator =
  val (avroGen, avroSer) = if avroSerialization then (List(AvroSerializerGenerator(), AvroFactories.caller()), List(Serializer.Avro)) else (Nil, Nil)
  val (jsonGen, jsonSer) =
    if jsonSerialization then (List(CirceJsonSerializerGenerator(), JsonCirceFactories.caller()), List(Serializer.Json)) else (Nil, Nil)

  val generators = Seq(
    CallerGenerator(),
    MethodToCaseClassGenerator(),
    EntryPointFactoryGenerator.caller()
  )
  new Generator(generatorConfig, generators ++ avroGen ++ jsonGen, avroSer ++ jsonSer, http4sClient)
