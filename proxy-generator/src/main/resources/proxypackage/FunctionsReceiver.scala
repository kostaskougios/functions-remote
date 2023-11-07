package {{proxypackage}}
{{#allImports}}
import {{.}}
{{/allImports}}

class {{className}}{{frameworkTypeArgFull}}(
  {{#functions}}
    {{functionN}}Deserializer: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{^isUnitReturnType}}
    {{functionN}}ReturnTypeSerializer: {{resultNNoFramework}} => Array[Byte],
    {{/isUnitReturnType}}
  {{/functions}}
    f: {{exportedTypeFull}}
):

  {{#functions}}
  {{#mapResults}}
  def {{functionN}}{{firstParamsAndParens}}(data: Array[Byte]): {{frameworkTypeArg}}[Array[Byte]] =
    val params = {{functionN}}Deserializer(data)
    f.{{functionN}}{{firstParamsCallAndParens}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}}).map: r=>
      {{^isUnitReturnType}}
      {{functionN}}ReturnTypeSerializer(r)
      {{/isUnitReturnType}}
      {{#isUnitReturnType}}Array.emptyByteArray{{/isUnitReturnType}}
  {{/mapResults}}
  {{^mapResults}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    val params = {{functionN}}Deserializer(data)
    {{^isUnitReturnType}}
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}ReturnTypeSerializer(r)
    {{/isUnitReturnType}}
    {{#isUnitReturnType}}
    f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    Array.emptyByteArray
    {{/isUnitReturnType}}
  {{/mapResults}}
  {{/functions}}
