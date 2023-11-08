package {{proxypackage}}

import functions.model.*

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
  // generic factories
{{{Generic}}}
  {{! ------------------------------------ Classloader transports ---------------------------------------------- }}
{{{IsolatedClassLoader}}}
  {{! ------------------------------------ Http4s transports      ---------------------------------------------- }}
  {{#generatorFactories.http4sClientTransport}}
  // Http4s factories
  import functions.http4s.Http4sTransport
  import org.http4s.client.Client
  import org.http4s.*
  {{#generatorFactories.serializers}}

  def newHttp4s{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(client: Client[{{frameworkTypeArg}}], serverUri: Uri) : {{exportedTypeFull}} =
    val transport = new Http4sTransport[{{frameworkTypeArg}}](client, serverUri)
    {{exportedType.name}}Caller{{serializer}}SerializedFactory.createCaller(transport.transportFunction)

  {{/generatorFactories.serializers}}
  {{/generatorFactories.http4sClientTransport}}