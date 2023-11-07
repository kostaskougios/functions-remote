package {{proxypackage}}
import functions.model.*

{{#imports}}
import {{.}}
{{/imports}}

object {{className}}:
  val JsonSerializer = new {{exportedType.name}}CirceJsonSerializer
  def createCaller{{frameworkTypeArgFull}}(transport: TransportInput => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}):  {{exportedTypeFull}}  =
    new {{exportedType.name}}Caller(
      {{#functions}}
      JsonSerializer.{{functionN}}Serializer,
      {{^isUnitReturnType}}
      JsonSerializer.{{functionN}}ReturnTypeDeserializer,
      {{/isUnitReturnType}}
      {{/functions}}
      transport,
      Serializer.Json
    )