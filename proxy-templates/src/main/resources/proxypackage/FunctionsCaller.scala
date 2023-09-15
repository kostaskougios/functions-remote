package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{className}}(
  {{#functions}}
  {{functionN}}ToByteArray: {{methodParams}}.{{caseClassName}} => Array[Byte],
  {{functionN}}ReturnTypeFromByteArray: Array[Byte] => {{resultN}},
  {{/functions}}
  callFunction: (String, Array[Byte]) => Array[Byte]
) extends {{exportedType.name}}:

  {{#functions}}
  // {{functionN}} function
  def {{functionN}}({{params}}): {{resultN}} =
    val c  = {{caseClass}}({{paramsCall}})
    val binIn = {{functionN}}ToByteArray(c)
    val binOut = callFunction({{methodParams}}.Methods.{{caseClassName}}, binIn)
    {{functionN}}ReturnTypeFromByteArray(binOut)
  {{/functions}}
