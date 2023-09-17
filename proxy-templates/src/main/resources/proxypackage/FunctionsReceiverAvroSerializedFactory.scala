package {{proxypackage}}

object {{className}}:
  def create{{exportedType.name}}Receiver(functions: {{exportedType.name}}): {{exportedType.name}}Receiver =
    val serializer = new {{exportedType.name}}AvroSerializer
    new {{exportedType.name}}Receiver(
      {{#functions}}
      serializer.{{functionN}}Deserializer,
      serializer.{{functionN}}ReturnTypeSerializer,
      {{/functions}}
      functions
    )