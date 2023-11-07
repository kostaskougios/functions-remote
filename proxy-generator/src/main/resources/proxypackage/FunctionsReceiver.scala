package {{proxypackage}}
{{#allImports}}
import {{.}}
{{/allImports}}

class {{className}}{{frameworkTypeArgFull}}(
  {{#functions}}
    {{functionN}}Deserializer: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{^returnType.isUnit}}
    {{functionN}}ReturnTypeSerializer: {{resultNNoFramework}} => Array[Byte],
    {{/returnType.isUnit}}
  {{/functions}}
    f: {{exportedTypeFull}}
):

  {{#functions}}
  {{#mapResults}}
  def {{functionN}}(data: Array[Byte]): {{frameworkTypeArg}}[Array[Byte]] =
    val params = {{functionN}}Deserializer(data)
    f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}}).map: r=>
      {{^returnType.isUnit}}
      {{functionN}}ReturnTypeSerializer(r)
      {{/returnType.isUnit}}
      {{#returnType.isUnit}}Array.emptyByteArray{{/returnType.isUnit}}
  {{/mapResults}}
  {{^mapResults}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    val params = {{functionN}}Deserializer(data)
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{^returnType.isUnit}}{{functionN}}ReturnTypeSerializer(r){{/returnType.isUnit}}
    {{#returnType.isUnit}}Array.emptyByteArray{{/returnType.isUnit}}
  {{/mapResults}}
  {{/functions}}
