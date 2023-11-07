package exported

import endtoend.tests.{NestedTypeParamsFunctionsImpl, NestedTypeParamsFunctionsReceiverFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.model.Coordinates4

import java.util.function.BiFunction

object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val receiver =
    SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl) ++
      NestedTypeParamsFunctionsReceiverFactory.invokerMap(new NestedTypeParamsFunctionsImpl)

  override def apply(method: String, data: Array[Byte]): Array[Byte] =
    val coordinates = Coordinates4(method)
    val function    = receiver(coordinates)
    function(data)
