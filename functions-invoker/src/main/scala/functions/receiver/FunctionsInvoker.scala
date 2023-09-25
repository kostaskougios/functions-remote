package functions.receiver

import functions.Log
import functions.model.{Coordinates, ReceiverFactory, Serializer}
import functions.receiver.model.{AvailableFunction, RegisteredFunction}
import functions.serializerscanners.GenericScanner

class FunctionsInvoker(availableFunctions: Seq[AvailableFunction]):
  def invoke(coordinates: String, data: Array[Byte]): Array[Byte] =
    Log.info(s"Invoking $coordinates")
    val c    = Coordinates(coordinates)
    val args = (c.methodAndVersion, data)
    val f    = availableFunctions
      .find(_.functionsReceiver.invoke.isDefinedAt(args))
      .getOrElse(throw new IllegalStateException(s"Can't find method ${c.methodAndVersion}"))

    f.functionsReceiver.invoke(args)

object FunctionsInvoker:
  def apply(classLoader: ClassLoader, functions: Seq[RegisteredFunction[_]]): FunctionsInvoker =
    val scanners           = Seq(GenericScanner[ReceiverFactory[_]](classLoader, Serializer.Avro, "ReceiverAvroSerializedFactory"))
    val availableFunctions = for
      f               <- functions
      s               <- scanners
      receiverFactory <- s.scan(f.className)
    yield (f.className, AvailableFunction(receiverFactory.asInstanceOf[ReceiverFactory[Any]].createReceiver(f.function), s.serializer))
    val missing            = functions.map(_.className).toSet -- availableFunctions.map(_._1).toSet
    if missing.nonEmpty then throw new IllegalStateException(s"Missing serializers for ${missing.mkString(", ")}")
    val afs                = availableFunctions.map(_._2)
    Log.info(s"Found these functions : ${afs.mkString(", ")}")
    new FunctionsInvoker(afs)

  def withFunctions(functions: RegisteredFunction[_]*) = apply(Thread.currentThread().getContextClassLoader, functions.toList)