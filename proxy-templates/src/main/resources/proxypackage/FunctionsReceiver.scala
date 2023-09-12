package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{functionsReceiver}}(
  {{#functions}}
    from{{caseClassName}}: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{functionN}}Response: {{resultN}} => Array[Byte],
  {{/functions}}
    f: LsFunctions
):
  def invoke(method: LsFunctionsMethods.Methods, data: Array[Byte]): Array[Byte] =
    method match
      case LsFunctionsMethods.Methods.Ls => ls(data)

  {{#functions}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    val params = from{{caseClassName}}(data)
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}Response(r)
  {{/functions}}
