package functions.receiver

import functions.model.{ReceiverFactory, Serializer}
import functions.receiver.model.{AvailableFunction, RegisteredFunction}
import functions.serializerscanners.GenericScanner

class FunctionsInvoker(availableFunctions: Seq[AvailableFunction]):
  def invoke(method: String, data: Array[Byte]): Array[Byte] = ???

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
    new FunctionsInvoker(availableFunctions.map(_._2))

  def withFunctions(functions: RegisteredFunction[_]*) = apply(Thread.currentThread().getContextClassLoader, functions.toList)
