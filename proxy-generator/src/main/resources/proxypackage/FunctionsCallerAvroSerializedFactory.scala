package {{proxypackage}}
import functions.model.Coordinates4
import functions.model.Serializer

{{#imports}}
import {{.}}
{{/imports}}

object {{className}}:
  val AvroSerializer = new {{exportedType.name}}AvroSerializer
  def createCaller{{frameworkTypeArgFull}}(transport: (Coordinates4, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}): {{exportedTypeFull}} =
    new {{exportedType.name}}Caller(
      {{#functions}}
      AvroSerializer.{{functionN}}Serializer,
      AvroSerializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport,
      Serializer.Avro
    )