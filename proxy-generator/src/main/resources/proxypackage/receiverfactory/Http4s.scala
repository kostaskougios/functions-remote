  {{#generatorFactories.http4sClientTransport}}
  // Http4s factories
  {{#generatorFactories.serializers}}
  // {{serializer}} serialized routes
  def new{{serializer}}{{exportedType.name}}Routes{{frameworkTypeArgFull}}(impl: {{exportedTypeFull}}): {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}} =
    val receiver = new{{serializer}}{{exportedType.name}}{{exportedTypeTypeArgs}}(impl)
    new {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}}(receiver, Serializer.{{serializer}})

  {{/generatorFactories.serializers}}
  {{/generatorFactories.http4sClientTransport}}
