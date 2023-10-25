package {{proxypackage}}
{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}} /* extends ReceiverFactory[{{exportedTypeFull}}] */:
  val Serializer = new {{exportedType.name}}CirceJsonSerializer
  def createReceiver{{frameworkTypeArgFull}}(functions: {{exportedTypeFull}}): {{exportedType.name}}Receiver{{exportedTypeTypeArgs}} =
    new {{exportedType.name}}Receiver(
      {{#functions}}
      Serializer.{{functionN}}Deserializer,
      Serializer.{{functionN}}ReturnTypeSerializer,
      {{/functions}}
      functions
    )