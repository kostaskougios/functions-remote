package exported

import endtoend.tests.{SimpleFunctions, SimpleFunctionsAvroSerializer, SimpleFunctionsImpl}
import functions.receiver.FunctionsInvoker
import functions.receiver.model.RegisteredFunction

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val functions = FunctionsInvoker.withFunctions(RegisteredFunction[SimpleFunctions](new SimpleFunctionsImpl))

  override def apply(method: String, data: Array[Byte]) = functions.invoke(method, data)
