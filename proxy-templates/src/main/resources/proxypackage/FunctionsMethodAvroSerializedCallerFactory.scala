package {{proxypackage}}

object {{className}}:
  def create{{exportedType.name}}Caller(transport: (String, Array[Byte]) => Array[Byte]) =
    val serializer = new {{exportedType.name}}MethodsAvroSerializer
    new {{exportedType.name}}Caller(
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )