package functions.receiver

import functions.model.{CallerFactory, Serializer}
import functions.receiver.model.RegisteredFunction
import functions.serializerscanners.{GenericScanner, SerializerScanner}

class FunctionsReceiver(registeredFunctions: Seq[RegisteredFunction[_]], serializerScanners: Seq[SerializerScanner[_]]):
  def invoke(method: String, data: Array[Byte]): Array[Byte] = ???

object FunctionsReceiver:
  def apply(classLoader: ClassLoader, functions: Seq[RegisteredFunction[_]]): FunctionsReceiver =
    val scanners = Seq(GenericScanner[CallerFactory[_]](classLoader, Serializer.Avro, "CallerAvroSerializedFactory"))
    new FunctionsReceiver(functions, scanners)

  def withDefaults(functions: RegisteredFunction[_]*) = apply(Thread.currentThread().getContextClassLoader, functions.toList)
