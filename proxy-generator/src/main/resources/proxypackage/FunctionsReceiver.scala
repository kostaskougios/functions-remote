package {{proxypackage}}
{{#allImports}}
import {{.}}
{{/allImports}}

class {{className}}{{frameworkTypeArgFull}}(
  {{#functions}}
    {{functionN}}Deserializer: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{functionN}}ReturnTypeSerializer: {{resultNNoFramework}} => Array[Byte],
  {{/functions}}
    f: {{exportedTypeFull}}
):

  {{#functions}}
  {{#mapResults}}
  def {{functionN}}(data: Array[Byte]): {{frameworkTypeArg}}[Array[Byte]] =
    val params = {{functionN}}Deserializer(data)
    f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}}).map: r=>
      {{functionN}}ReturnTypeSerializer(r)
  {{/mapResults}}
  {{^mapResults}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    val params = {{functionN}}Deserializer(data)
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}ReturnTypeSerializer(r)
  {{/mapResults}}
  {{/functions}}
