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

  {{! ------------------------------------ Classloader transports ---------------------------------------------- }}
  {{#generatorFactories.isClassloader}}
  import functions.model.RuntimeConfig
  import functions.transports.SeparateClassLoaderTransport

  def newClassloaderBuilder(runtimeConfig: RuntimeConfig): ClassloaderBuilder = new ClassloaderBuilder(runtimeConfig)
  class ClassloaderBuilder(runtimeConfig: RuntimeConfig):
    // we need 1 classloader transport so that we load this function only once
    val classLoader = new SeparateClassLoaderTransport(runtimeConfig)
    val transport = classLoader.createTransport(BuildInfo.organization, BuildInfo.exportedArtifact, BuildInfo.version)
    {{#generatorFactories.serializers}}
    def new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}: {{exportedTypeFull}} =
      {{className}}.new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(transport)
    {{/generatorFactories.serializers}}

  {{/generatorFactories.isClassloader}}
  {{! ------------------------------------ Http4s transports      ---------------------------------------------- }}
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