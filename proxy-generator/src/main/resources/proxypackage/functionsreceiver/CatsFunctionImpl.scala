  {{#mapResults}}
  def {{functionN}}{{firstParamsAndParens}}(data: Array[Byte]): {{frameworkTypeArg}}[Array[Byte]] =
    {{#paramsRaw.isEmpty}}if data.length>0 then throw new IllegalStateException(s"Expected empty array of bytes but got ${data.length}"){{/paramsRaw.isEmpty}}
    {{^paramsRaw.isEmpty}}val params = {{functionN}}Deserializer(data) {{/paramsRaw.isEmpty}}
    f.{{functionN}}{{firstParamsCallAndParens}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}}).map: r=>
      {{^isUnitReturnType}}
      {{functionN}}ReturnTypeSerializer(r)
      {{/isUnitReturnType}}
      {{#isUnitReturnType}}Array.emptyByteArray{{/isUnitReturnType}}
  {{/mapResults}}
