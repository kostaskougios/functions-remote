package exported

import functions.receiver.FunctionsReceiver
import functions.receiver.model.RegisteredFunction
import ls.LsFunctions
import ls.impl.LsFunctionsImpl

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val functions = FunctionsReceiver(RegisteredFunction[LsFunctions](new LsFunctionsImpl))
//  private val receiver = LsFunctionsReceiverAvroSerializedFactory.createReceiver()

  override def apply(method: String, data: Array[Byte]) = functions.invoke(method, data)
