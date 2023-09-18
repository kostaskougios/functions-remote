package {{proxypackage}}

object {{className}}:
  def createCaller(transport: (String, Array[Byte]) => Array[Byte]): PartialFunction[String, {{exportedType.name}}] =
      {{#functions}}
      case {{exportedType.name}}Methods.Methods.{{caseClassName}} => create{{exportedType.name}}Caller(transport)
      {{/functions}}

  def create{{exportedType.name}}Caller(transport: (String, Array[Byte]) => Array[Byte]): {{exportedType.name}} =
    val serializer = new {{exportedType.name}}AvroSerializer
    new {{exportedType.name}}Caller(
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )