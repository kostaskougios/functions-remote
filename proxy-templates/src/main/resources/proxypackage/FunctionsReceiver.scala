package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{functionsProxy}}(
  {{#functions}}
    from{{caseClassName}}: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{functionN}}Response: {{resultN}} => Array[Byte],
  {{/functions}}
    f: LsFunctions
):
  def invoke(method: LsFunctionsMethods.Methods, data: Array[Byte]): Array[Byte] =
    method match
    {{#functions}}
      case {{methodParams}}.Methods.{{caseClassName}} => {{functionN}}(data)
    {{/functions}}

  {{#functions}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    val params = from{{caseClassName}}(data)
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}Response(r)
  {{/functions}}
