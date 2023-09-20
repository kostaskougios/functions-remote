package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{className}}(
  {{#functions}}
    {{functionN}}Deserializer: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{functionN}}ReturnTypeSerializer: {{resultN}} => Array[Byte],
  {{/functions}}
    f: LsFunctions
):
  def invoke: PartialFunction[( String, Array[Byte]) , Array[Byte]] =
    {{#functions}}
      case ({{methodParams}}.Methods.{{caseClassName}}, data) => {{functionN}}(data)
    {{/functions}}

  {{#functions}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    val params = {{functionN}}Deserializer(data)
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}ReturnTypeSerializer(r)
  {{/functions}}
