package {{proxypackage}}
import functions.model.CallerFactory
import functions.model.Serializer
{{#imports}}
import {{.}}
{{/imports}}

object {{className}} /* extends CallerFactory[{{exportedType.name}}] */:
  def createCaller{{frameworkTypeArgFull}}(transport: (String, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}):  {{exportedTypeFull}}  =
    val serializer = new {{exportedType.name}}CirceJsonSerializer
    new {{exportedType.name}}Caller(
      Serializer.Json,
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )