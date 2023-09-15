package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{className}}(
  {{#functions}}
  // {{functionN}} serialization
  {{functionN}}ToByteArray: {{methodParams}}.{{caseClassName}} => Array[Byte],
  {{functionN}}ReturnTypeFromByteArray: Array[Byte] => {{resultN}},
  {{/functions}}
  // this should transport the data to the remote function and get the response from that function
  transport: (String, Array[Byte]) => Array[Byte]
) extends {{exportedType.name}}:

  {{#functions}}
  // {{functionN}} function
  def {{functionN}}({{params}}): {{resultN}} =
    val c  = {{caseClass}}({{paramsCall}})
    val binIn = {{functionN}}ToByteArray(c)
    val binOut = transport({{methodParams}}.Methods.{{caseClassName}}, binIn)
    {{functionN}}ReturnTypeFromByteArray(binOut)
  {{/functions}}
