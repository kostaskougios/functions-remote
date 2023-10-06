package exported

import endtoend.tests.{NestedTypeParamsFunctions, NestedTypeParamsFunctionsImpl, SimpleFunctions, SimpleFunctionsImpl}
import functions.receiver.FunctionsInvoker
import functions.receiver.model.RegisteredFunction

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val functions = FunctionsInvoker.withFunctions(
    RegisteredFunction[SimpleFunctions](new SimpleFunctionsImpl),
    RegisteredFunction[NestedTypeParamsFunctions](new NestedTypeParamsFunctionsImpl)
  )

  override def apply(method: String, data: Array[Byte]) = functions.invoke(method, data)
