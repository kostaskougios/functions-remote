  {{#generatorFactories.serializers}}
  def new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(transport : TransportInput => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}) : {{exportedTypeFull}} =
    {{exportedType.name}}Caller{{serializer}}SerializedFactory.createCaller(transport, true)
  {{/generatorFactories.serializers}}
