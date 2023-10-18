package {{proxypackage}}
import functions.model.CallerFactory
import functions.model.Serializer
{{#imports}}
import {{.}}
{{/imports}}

object {{className}} /* extends CallerFactory[{{exportedType.name}}] */:
  def createCaller{{frameworkTypeArgFull}}(transport: (String, Array[Byte]) => Array[Byte]): {{exportedTypeFull}} =
    val serializer = new {{exportedType.name}}AvroSerializer
    new {{exportedType.name}}Caller(
      Serializer.Avro,
      {{#functions}}
      serializer.{{functionN}}Serializer,
      serializer.{{functionN}}ReturnTypeDeserializer,
      {{/functions}}
      transport
    )