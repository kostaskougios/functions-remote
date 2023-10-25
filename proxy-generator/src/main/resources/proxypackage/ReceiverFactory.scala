package {{proxypackage}}

import functions.model.Serializer

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
  // generic factories
  {{#generatorFactories.serializers}}
  def new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(impl: {{exportedTypeFull}}) : {{exportedType.name}}Receiver{{exportedTypeTypeArgs}} =
    {{exportedType.name}}Receiver{{serializer}}SerializedFactory.createReceiver(impl)
  {{/generatorFactories.serializers}}

  {{^exportedType.hasFramework}}
  import functions.model.Coordinates3
  def invokerMap(impl: {{exportedTypeFull}}): Map[Coordinates3, Array[Byte] => Array[Byte]] =
    {{#generatorFactories.serializers}}
    invoker{{serializer}}Map(impl) {{^last}} ++ {{/last}}
    {{/generatorFactories.serializers}}

  {{#generatorFactories.serializers}}
  def invoker{{serializer}}Map(impl: {{exportedTypeFull}}): Map[Coordinates3, Array[Byte] => Array[Byte]] =
    val i = new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(impl)
    val s = Serializer.valueOf("{{serializer}}")
    Map(
    {{#functions}}
      Coordinates3({{exportedType.name}}Methods.Methods.{{caseClassName}}, s) -> i.{{functionN}}{{^last}},{{/last}}
    {{/functions}}
    )
  {{/generatorFactories.serializers}}
  {{/exportedType.hasFramework}}

  {{! ------------------------------------ Http4s ---------------------------------------------- }}
  {{#generatorFactories.isHttp4s}}
  // Http4s factories
  {{#generatorFactories.serializers}}
  // {{.}} serialized routes
  def new{{serializer}}{{exportedType.name}}Routes{{frameworkTypeArgFull}}(impl: {{exportedTypeFull}}): {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}} =
    val receiver = new{{serializer}}{{exportedType.name}}{{exportedTypeTypeArgs}}(impl)
    new {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}}(receiver, Serializer.{{serializer}})

  {{/generatorFactories.serializers}}
  {{/generatorFactories.isHttp4s}}