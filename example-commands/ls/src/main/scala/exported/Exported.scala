package exported

import commands.ls.LsFunctions
import commands.model.LsOptions
import functions.receiver.FunctionsInvoker
import functions.receiver.model.RegisteredFunction
import commands.ls.LsFunctionsMethods.Ls
import commands.ls.{LsFunctionsAvroSerializer, LsFunctionsMethods}
import commands.ls.impl.LsFunctionsImpl

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val functions = FunctionsInvoker.withFunctions(RegisteredFunction[LsFunctions](new LsFunctionsImpl))

  override def apply(method: String, data: Array[Byte]) = functions.invoke(method, data)

@main
def tryExporter() =
  val serializer = new LsFunctionsAvroSerializer
  val result     = Exported(LsFunctionsMethods.Methods.Ls + ":Avro", serializer.lsSerializer(Ls("/tmp", LsOptions())))
  val cc         = serializer.lsReturnTypeDeserializer(result)
  println(cc)
