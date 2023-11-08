  // generic factories
  {{#generatorFactories.serializers}}
  def new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(impl: {{exportedTypeFull}}) : {{exportedType.name}}Receiver{{exportedTypeTypeArgs}} =
    {{exportedType.name}}Receiver{{serializer}}SerializedFactory.createReceiver(impl)
  {{/generatorFactories.serializers}}

  {{^exportedType.hasFramework}}
  import functions.model.Coordinates4
  def invokerMap(impl: {{exportedTypeFull}}): Map[Coordinates4, Array[Byte] => Array[Byte]] =
    {{#generatorFactories.serializers}}
    invoker{{serializer}}Map(impl) {{^last}} ++ {{/last}}
    {{/generatorFactories.serializers}}

  {{#generatorFactories.serializers}}
  def invoker{{serializer}}Map(impl: {{exportedTypeFull}}): Map[Coordinates4, Array[Byte] => Array[Byte]] =
    val i = new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(impl)
    val s = Serializer.valueOf("{{serializer}}")
    Map(
    {{#functions}}
      Coordinates4({{exportedType.name}}Methods.Methods.{{caseClassName}}, s) -> i.{{functionN}}{{^last}},{{/last}}
    {{/functions}}
    )
  {{/generatorFactories.serializers}}
  {{/exportedType.hasFramework}}
