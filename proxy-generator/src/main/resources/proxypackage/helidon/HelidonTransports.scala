  {{#generatorFactories.helidon}}
  import functions.helidon.transport.HelidonTransport

  {{#generatorFactories.serializers}}
  def newHelidon{{serializer}}{{exportedType.name}}(transport : HelidonTransport) : {{exportedTypeFull}} =
    {{exportedType.name}}Caller{{serializer}}SerializedFactory.createCaller(transport.transportFunction, false)
  {{/generatorFactories.serializers}}
  {{/generatorFactories.helidon}}
