package {{proxypackage}}
{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  val Serializer = new {{exportedType.name}}AvroSerializer
  def createReceiver{{frameworkTypeArgFull}}(functions: {{exportedTypeFull}}): {{exportedType.name}}Receiver{{exportedTypeTypeArgs}} =
    new {{exportedType.name}}Receiver(
      {{#functions}}
      Serializer.{{functionN}}Deserializer,
      {{^firstParamsRaw.isEmpty}}
      Serializer.{{functionN}}ArgsDeserializer,
      {{/firstParamsRaw.isEmpty}}
      {{^isUnitReturnType}}
      Serializer.{{functionN}}ReturnTypeSerializer,
      {{/isUnitReturnType}}
      {{/functions}}
      functions
    )