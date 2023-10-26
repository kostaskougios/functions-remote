package {{proxypackage}}

import functions.model.Coordinates3

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
  // generic factories
  {{#generatorFactories.serializers}}
  def new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(transport : (Coordinates3, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}) : {{exportedTypeFull}} =
    {{exportedType.name}}Caller{{serializer}}SerializedFactory.createCaller(transport)
  {{/generatorFactories.serializers}}

  {{! ------------------------------------ Http4s ---------------------------------------------- }}
  {{#generatorFactories.isHttp4s}}
  // Http4s factories
  import functions.http4s.Http4sTransport
  import org.http4s.client.Client
  import org.http4s.*
  {{#generatorFactories.serializers}}

  def newHttp4s{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(client: Client[{{frameworkTypeArg}}], serverUri: Uri) : {{exportedTypeFull}} =
    val transport = new Http4sTransport[{{frameworkTypeArg}}](client, serverUri)
    {{exportedType.name}}Caller{{serializer}}SerializedFactory.createCaller(transport.transportFunction)

  {{/generatorFactories.serializers}}
  {{/generatorFactories.isHttp4s}}