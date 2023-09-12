package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{functionsProxy}}(
  toByteArray: {{methodParams}} => Array[Byte],
  callFunction: ({{methodParams}}.Methods, Array[Byte]) => Any
):

  {{#functions}}
  // {{functionN}} function
  def {{functionN}}({{params}}): {{resultN}} =
    val c  = {{caseClass}}({{paramsCall}})
    val r1 = toByteArray(c)
    val r2 = callFunction({{methodParams}}.Methods.{{caseClassName}}, r1)
    r2.asInstanceOf[{{resultN}}]
  {{/functions}}
