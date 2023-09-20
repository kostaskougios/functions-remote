package exported

import ls.LsFunctionsReceiverAvroSerializedFactory
import ls.impl.LsFunctionsImpl

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val receiver                                  = LsFunctionsReceiverAvroSerializedFactory.createReceiver(new LsFunctionsImpl)
  override def apply(method: String, data: Array[Byte]) = receiver.invoke(method, data)
