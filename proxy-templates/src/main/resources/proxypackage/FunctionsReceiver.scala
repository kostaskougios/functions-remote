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

  def ls(data: Array[Byte]): Array[Byte] =
    val params = fromLs(data)
    val r      = f.ls(params.path, params.lsOptions)
    lsResponse(r)
