package {{proxypackage}}

import functions.model.ReceiverFactory
{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}} /* extends ReceiverFactory[{{exportedTypeFull}}] */:
  def createReceiver{{frameworkTypeArgFull}}(functions: {{exportedTypeFull}}): {{exportedType.name}}Receiver{{exportedTypeTypeArgs}} =
    val serializer = new {{exportedType.name}}AvroSerializer
    new {{exportedType.name}}Receiver(
      {{#functions}}
      serializer.{{functionN}}Deserializer,
      serializer.{{functionN}}ReturnTypeSerializer,
      {{/functions}}
      functions
    )