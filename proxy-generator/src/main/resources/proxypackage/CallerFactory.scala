package {{proxypackage}}

import functions.model.Coordinates2
{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{#generatorFactories.serializers}}
  def new{{.}}{{exportedType.name}}{{frameworkTypeArgFull}}(transport : (coordinates: Coordinates2, data: Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}}) : {{exportedTypeFull}} =
    {{exportedType.name}}Caller{{.}}SerializedFactory.createCaller(transport)
  {{/generatorFactories.serializers}}