  {{#generatorFactories.helidon}}
  import functions.helidon.requests.ResponseProcessor
  // helidon factories
  {{#generatorFactories.serializers}}
  // {{serializer}} serialized routes
  def new{{serializer}}{{exportedType.name}}HelidonRoutes(impl: {{exportedTypeFull}}, processor: ResponseProcessor = new ResponseProcessor(Serializer.{{serializer}})): {{exportedType.name}}HelidonRoutes =
    val receiver = new{{serializer}}{{exportedType.name}}{{exportedTypeTypeArgs}}(impl)
    new {{exportedType.name}}HelidonRoutes{{exportedTypeTypeArgs}}(receiver, Serializer.{{serializer}}, processor)

  {{/generatorFactories.serializers}}
  {{/generatorFactories.helidon}}
