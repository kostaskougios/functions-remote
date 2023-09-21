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
    yield AvailableFunction(receiverFactory.asInstanceOf[ReceiverFactory[Any]].createReceiver(f.function), s.serializer)
    new FunctionsInvoker(availableFunctions)

  def withFunctions(functions: RegisteredFunction[_]*) = apply(Thread.currentThread().getContextClassLoader, functions.toList)
