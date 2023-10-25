package exported

import endtoend.tests.{SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.model.Coordinates3

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val receiver = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)

  override def apply(method: String, data: Array[Byte]): Array[Byte] =
    val coordinates3 = Coordinates3(method)
    val function     = receiver(coordinates3)
    function(data)
