package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{functionsCaller}}(
  {{function1}}: {{methodParams}} => {{function1ReturnType}},
  {{function2}}: ({{methodParams}}.Methods, {{function1ReturnType}}) => Any
):

  {{#functions}}
  // {{functionN}} function
  def {{functionN}}({{params}}): {{resultN}} =
    val c  = {{caseClass}}({{paramsCall}})
    val r1 = {{function1}}(c)
    val r2 = {{function2}}({{methodParams}}.Methods.{{caseClassName}}, r1)
    r2.asInstanceOf[{{resultN}}]
  {{/functions}}
