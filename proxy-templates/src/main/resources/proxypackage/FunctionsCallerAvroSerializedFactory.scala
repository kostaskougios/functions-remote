package {{proxypackage}}

object {{className}}:
  def createCaller(transport: (String, Array[Byte]) => Array[Byte]): {{exportedType.name}} =
    val serializer = new {{exportedType.name}}AvroSerializer
    new {{exportedType.name}}Caller(
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )