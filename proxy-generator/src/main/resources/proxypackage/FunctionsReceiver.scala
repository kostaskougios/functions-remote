package {{proxypackage}}

import functions.model.ReceiverInput
{{#allImports}}
import {{.}}
{{/allImports}}

class {{className}}{{frameworkTypeArgFull}}(
  {{#functions}}
    {{functionN}}Deserializer: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{^firstParamsRaw.isEmpty}}
    {{functionN}}ArgsDeserializer: Array[Byte] => {{methodParams}}.{{caseClassName}}Args,
    {{/firstParamsRaw.isEmpty}}
    {{^isUnitReturnType}}
    {{functionN}}ReturnTypeSerializer: {{resultNNoFramework}} => Array[Byte],
    {{/isUnitReturnType}}
  {{/functions}}
    f: {{exportedTypeFull}}
):

  {{#functions}}
{{{CatsFunctionImpl}}}{{{NoFrameworkImpl}}}
  {{/functions}}
