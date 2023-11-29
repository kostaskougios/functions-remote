  {{#generatorFactories.http4sClientTransport}}
  // Http4s factories
  {{#generatorFactories.serializers}}
  import functions.http4s.Http4sRoutes
  // {{serializer}} serialized routes
  def new{{serializer}}{{exportedType.name}}Routes{{frameworkTypeArgFull}}(
    impl: {{exportedTypeFull}},
    http4sRoutes: Option[Http4sRoutes{{exportedTypeTypeArgs}}] = None
  ): {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}} =
    val r = http4sRoutes.getOrElse(new Http4sRoutes{{exportedTypeTypeArgs}}(Serializer.{{serializer}}))
    val receiver = new{{serializer}}{{exportedType.name}}{{exportedTypeTypeArgs}}(impl)
    new {{exportedType.name}}Http4sRoutes{{exportedTypeTypeArgs}}(receiver, r)

  {{/generatorFactories.serializers}}
  {{/generatorFactories.http4sClientTransport}}
