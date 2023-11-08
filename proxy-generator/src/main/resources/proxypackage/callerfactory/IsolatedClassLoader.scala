  {{#generatorFactories.isClassloader}}
  // IsolatedClassLoader factories
  import functions.environment.RuntimeConfig
  import functions.transports.IsolatedClassLoaderTransport

  def newIsolatedClassloaderBuilder(runtimeConfig: RuntimeConfig): IsolatedClassloaderBuilder = new IsolatedClassloaderBuilder(runtimeConfig)
  class IsolatedClassloaderBuilder(runtimeConfig: RuntimeConfig):
    val classLoader = new IsolatedClassLoaderTransport(runtimeConfig)
    // we need to reuse this classloader transport so that we class-load this function only once
    val transport = classLoader.createTransport(BuildInfo.organization, BuildInfo.exportedArtifact, BuildInfo.version)
    {{#generatorFactories.serializers}}
    def new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}: {{exportedTypeFull}} =
      {{className}}.new{{serializer}}{{exportedType.name}}{{frameworkTypeArgFull}}(transport)
    {{/generatorFactories.serializers}}

  {{/generatorFactories.isClassloader}}
