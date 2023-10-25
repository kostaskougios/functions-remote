package {{proxypackage}}
import functions.model.Coordinates2
{{#imports}}
import {{.}}
{{/imports}}

object {{className}}:
  val Serializer = new {{exportedType.name}}AvroSerializer
  def createCaller{{frameworkTypeArgFull}}(transport: (Coordinates2, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}): {{exportedTypeFull}} =
    new {{exportedType.name}}Caller(
      {{#functions}}
      Serializer.{{functionN}}Serializer,
      Serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )