package {{proxypackage}}

import functions.model.Serializer

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
  // generic factories
  {{#generatorFactories.serializers}}
  def new{{.}}{{exportedType.name}}{{frameworkTypeArgFull}}(impl: {{exportedTypeFull}}) : {{exportedType.name}}Receiver{{exportedTypeTypeArgs}} =
    {{exportedType.name}}Receiver{{.}}SerializedFactory.createReceiver(impl)
  {{/generatorFactories.serializers}}

  {{! ------------------------------------ Http4s ---------------------------------------------- }}
  {{#generatorFactories.isHttp4s}}
  // Http4s factories
  {{#generatorFactories.serializers}}
  // {{.}} serialized routes
  def new{{.}}{{exportedType.name}}Routes{{frameworkTypeArgFull}}(impl: {{exportedTypeFull}}): {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}} =
    val receiver = new{{.}}{{exportedType.name}}{{exportedTypeTypeArgs}}(impl)
    new {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}}(receiver, Serializer.{{.}})

  {{/generatorFactories.serializers}}
  {{/generatorFactories.isHttp4s}}