package {{proxypackage}}
import functions.model.*

{{#imports}}
import {{.}}
{{/imports}}

object {{className}}:
  val AvroSerializer = new {{exportedType.name}}AvroSerializer
  def createCaller{{frameworkTypeArgFull}}(
    transport: TransportInput => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}},
    serializeArgs: Boolean
  ): {{exportedTypeFull}} =
    new {{exportedType.name}}Caller(
      {{#functions}}
      AvroSerializer.{{functionN}}Serializer,
      {{^firstParamsRaw.isEmpty}}
      AvroSerializer.{{functionN}}ArgsSerializer,
      {{/firstParamsRaw.isEmpty}}
      {{^isUnitReturnType}}
      AvroSerializer.{{functionN}}ReturnTypeDeserializer,
      {{/isUnitReturnType}}
      {{/functions}}
      transport,
      Serializer.Avro,
      serializeArgs
    )