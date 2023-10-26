package {{proxypackage}}
import functions.model.Coordinates3
import functions.model.Serializer

{{#imports}}
import {{.}}
{{/imports}}

object {{className}}:
  val JsonSerializer = new {{exportedType.name}}CirceJsonSerializer
  def createCaller{{frameworkTypeArgFull}}(transport: (Coordinates3, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}):  {{exportedTypeFull}}  =
    new {{exportedType.name}}Caller(
      {{#functions}}
      JsonSerializer.{{functionN}}Serializer,
      JsonSerializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport,
      Serializer.Json
    )