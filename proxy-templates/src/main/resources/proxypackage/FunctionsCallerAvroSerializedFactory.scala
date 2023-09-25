package {{proxypackage}}
import functions.model.CallerFactory
import functions.model.Serializer

object {{className}} extends CallerFactory[{{exportedType.name}}]:
  override def createCaller(transport: (String, Array[Byte]) => Array[Byte]): {{exportedType.name}} =
    val serializer = new {{exportedType.name}}AvroSerializer
    new {{exportedType.name}}Caller(
      Serializer.Avro,
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )