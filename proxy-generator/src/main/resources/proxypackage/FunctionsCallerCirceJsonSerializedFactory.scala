package {{proxypackage}}
import functions.model.CallerFactory
import functions.model.Serializer

object {{className}} extends CallerFactory[{{exportedType.name}}]:
  override def createCaller(transport: (String, Array[Byte]) => Array[Byte]): {{exportedType.name}} =
    val serializer = new {{exportedType.name}}CirceJsonSerializer
    new {{exportedType.name}}Caller(
      Serializer.Json,
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )