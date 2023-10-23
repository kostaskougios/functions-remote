package {{proxypackage}}
import functions.model.CallerFactory
import functions.model.Coordinates2
{{#imports}}
import {{.}}
{{/imports}}

object {{className}} /* extends CallerFactory[{{exportedType.name}}] */:
  def createCaller{{frameworkTypeArgFull}}(transport: (Coordinates2, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}):  {{exportedTypeFull}}  =
    val serializer = new {{exportedType.name}}CirceJsonSerializer
    new {{exportedType.name}}Caller(
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )