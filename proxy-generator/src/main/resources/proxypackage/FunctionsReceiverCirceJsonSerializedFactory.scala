package {{proxypackage}}

import functions.model.ReceiverFactory

object {{className}} extends ReceiverFactory[{{exportedTypeFull}}]:
  def createReceiver(functions: {{exportedTypeFull}}): {{exportedType.name}}Receiver =
    val serializer = new {{exportedType.name}}CirceJsonSerializer
    new {{exportedType.name}}Receiver(
      {{#functions}}
      serializer.{{functionN}}Deserializer,
      serializer.{{functionN}}ReturnTypeSerializer,
      {{/functions}}
      functions
    )