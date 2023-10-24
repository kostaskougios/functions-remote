package {{proxypackage}}

import functions.model.Coordinates2
import functions.model.Serializer

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
  // generic factories
  {{#generatorFactories.serializers}}
  def new{{.}}{{exportedType.name}}{{frameworkTypeArgFull}}(transport : (Coordinates2, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}) : {{exportedTypeFull}} =
    {{exportedType.name}}Caller{{.}}SerializedFactory.createCaller(transport)
  {{/generatorFactories.serializers}}

  {{! ------------------------------------ Http4s ---------------------------------------------- }}
  {{#generatorFactories.isHttp4s}}
  // Http4s factories
  import functions.http4s.Http4sTransport
  import org.http4s.client.Client
  import org.http4s.*
  {{#generatorFactories.serializers}}

  def newHttp4s{{.}}{{exportedType.name}}{{frameworkTypeArgFull}}(client: Client[{{frameworkTypeArg}}], serverUri: Uri) : {{exportedTypeFull}} =
    val transport = new Http4sTransport[{{frameworkTypeArg}}](client, serverUri, Serializer.{{.}})
    {{exportedType.name}}Caller{{.}}SerializedFactory.createCaller(transport.transportFunction)

  {{/generatorFactories.serializers}}
  {{/generatorFactories.isHttp4s}}