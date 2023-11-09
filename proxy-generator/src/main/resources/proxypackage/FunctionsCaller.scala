package {{proxypackage}}

import functions.model.Serializer
import functions.model.*
{{#allImports}}
import {{.}}
{{/allImports}}

class {{className}}{{frameworkTypeArgFull}}(
  {{#functions}}
  // {{functionN}} serialization
  {{functionN}}ToByteArray: {{methodParams}}.{{caseClassName}} => Array[Byte],
  {{^firstParamsRaw.isEmpty}}
  {{functionN}}ArgsToByteArray: {{methodParams}}.{{caseClassName}}Args => Array[Byte],
  {{/firstParamsRaw.isEmpty}}
  {{^isUnitReturnType}}
  {{functionN}}ReturnTypeFromByteArray: Array[Byte] => {{resultNNoFramework}},
  {{/isUnitReturnType}}
  {{/functions}}
  // this should transport the data to the remote function and get the response from that function
  transport: TransportInput => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}},
  serializer: Serializer,
  serializeArgs: Boolean
) extends {{exportedTypeFull}}:

  {{#functions}}
{{{CatsFunctionImpl}}}
{{{NoFrameworkImpl}}}
  {{/functions}}
