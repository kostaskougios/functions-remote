package {{proxypackage}}

import functions.model.Serializer
import functions.model.Coordinates4
{{#allImports}}
import {{.}}
{{/allImports}}

class {{className}}{{frameworkTypeArgFull}}(
  {{#functions}}
  // {{functionN}} serialization
  {{functionN}}ToByteArray: {{methodParams}}.{{caseClassName}} => Array[Byte],
  {{functionN}}ReturnTypeFromByteArray: Array[Byte] => {{resultNNoFramework}},
  {{/functions}}
  // this should transport the data to the remote function and get the response from that function
  transport: (Coordinates4, Array[Byte]) => {{frameworkTypeArgOpen}}Array[Byte]{{frameworkTypeArgClose}},
  serializer: Serializer
) extends {{exportedTypeFull}}:

  {{#functions}}
  // {{functionN}} function
  def {{functionN}}({{params}}): {{resultN}} =
    val c  = {{caseClass}}({{paramsCall}})
    val binIn = {{functionN}}ToByteArray(c)
    {{#exportedType.hasFramework}}
    transport({{methodParams}}.Methods.{{caseClassName}}.withSerializer(serializer), binIn).map: binOut=>
      {{functionN}}ReturnTypeFromByteArray(binOut)
    {{/exportedType.hasFramework}}
    {{^exportedType.hasFramework}}
    val binOut = transport({{methodParams}}.Methods.{{caseClassName}}.withSerializer(serializer), binIn)
    {{functionN}}ReturnTypeFromByteArray(binOut)
    {{/exportedType.hasFramework}}
  {{/functions}}
