package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{className}}(
  {{#functions}}
  {{functionN}}ToByteArray: {{methodParams}}.{{caseClassName}} => Array[Byte],
  {{/functions}}
  callFunction: (String, Array[Byte]) => Any
):

  {{#functions}}
  // {{functionN}} function
  def {{functionN}}({{params}}): {{resultN}} =
    val c  = {{caseClass}}({{paramsCall}})
    val r1 = {{functionN}}ToByteArray(c)
    val r2 = callFunction({{methodParams}}.Methods.{{caseClassName}}, r1)
    r2.asInstanceOf[{{resultN}}]
  {{/functions}}
