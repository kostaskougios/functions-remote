  {{#generatorFactories.helidon}}
  // helidon factories
  {{#generatorFactories.serializers}}
  // {{serializer}} serialized routes
  def new{{serializer}}{{exportedType.name}}HelidonRoutes(impl: {{exportedTypeFull}}): {{exportedType.name}}HelidonRoutes =
    val receiver = new{{serializer}}{{exportedType.name}}{{exportedTypeTypeArgs}}(impl)
    new {{exportedType.name}}HelidonRoutes{{exportedTypeTypeArgs}}(receiver, Serializer.{{serializer}})

  {{/generatorFactories.serializers}}
  {{/generatorFactories.helidon}}
