package {{proxypackage}}
import functions.model.Coordinates4
import functions.model.Serializer

{{#imports}}
import {{.}}
{{/imports}}

object {{className}}:
  val JsonSerializer = new {{exportedType.name}}CirceJsonSerializer
  def createCaller{{frameworkTypeArgFull}}(transport: (Coordinates4, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}):  {{exportedTypeFull}}  =
    new {{exportedType.name}}Caller(
      {{#functions}}
      JsonSerializer.{{functionN}}Serializer,
      {{^returnType.isUnit}}
      JsonSerializer.{{functionN}}ReturnTypeDeserializer,
      {{/returnType.isUnit}}
      {{/functions}}
      transport,
      Serializer.Json
    )