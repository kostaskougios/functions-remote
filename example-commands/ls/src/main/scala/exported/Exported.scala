package exported

import functions.receiver.FunctionsInvoker
import functions.receiver.model.RegisteredFunction
import ls.LsFunctionsMethods.Ls
import ls.{LsFunctions, LsFunctionsAvroSerializer, LsFunctionsMethods}
import ls.impl.LsFunctionsImpl
import ls.model.LsOptions

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val functions = FunctionsInvoker.withFunctions(RegisteredFunction[LsFunctions](new LsFunctionsImpl))

  override def apply(method: String, data: Array[Byte]) = functions.invoke(method, data)

@main
def tryExporter() =
  val serializer = new LsFunctionsAvroSerializer
  Exported(LsFunctionsMethods.Methods.Ls, serializer.lsSerializer(Ls("/tmp", LsOptions())))
