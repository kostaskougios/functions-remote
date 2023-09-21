package exported

import functions.receiver.FunctionsInvoker
import functions.receiver.model.RegisteredFunction
import ls.LsFunctions
import ls.impl.LsFunctionsImpl

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val functions = FunctionsInvoker.withFunctions(RegisteredFunction[LsFunctions](new LsFunctionsImpl))

  override def apply(method: String, data: Array[Byte]) = functions.invoke(method, data)
