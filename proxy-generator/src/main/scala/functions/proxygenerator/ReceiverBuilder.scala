package functions.proxygenerator

import functions.model.{GeneratorConfig, Serializer}
import functions.proxygenerator.codegenerators.*
import functions.proxygenerator.codegenerators.http4s.RoutesGenerator

def generateReceiver(
    generatorConfig: GeneratorConfig,
    avroSerialization: Boolean = false,
    jsonSerialization: Boolean = false,
    http4sRoutes: Boolean = false
): Generator =
  val (avroGen, avroSer) = if avroSerialization then (List(AvroSerializerGenerator(), AvroFactories.receiver()), List(Serializer.Avro)) else (Nil, Nil)
  val (jsonGen, jsonSer) =
    if jsonSerialization then (List(CirceJsonSerializerGenerator(), JsonCirceFactories.receiver()), List(Serializer.Json)) else (Nil, Nil)

  val http4sRoutesGen = if http4sRoutes then List(RoutesGenerator()) else Nil

  val generators = Seq(
    ReceiverGenerator(),
    MethodToCaseClassGenerator(),
    EntryPointFactoryGenerator.receiver()
  )
  new Generator(generatorConfig, generators ++ avroGen ++ jsonGen ++ http4sRoutesGen, avroSer ++ jsonSer, http4sRoutes)
