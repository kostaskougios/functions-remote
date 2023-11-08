  {{#mapResults}}
  def {{functionN}}{{firstParamsAndParens}}(data: Array[Byte]): {{frameworkTypeArg}}[Array[Byte]] =
    val params = {{functionN}}Deserializer(data)
    f.{{functionN}}{{firstParamsCallAndParens}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}}).map: r=>
      {{^isUnitReturnType}}
      {{functionN}}ReturnTypeSerializer(r)
      {{/isUnitReturnType}}
      {{#isUnitReturnType}}Array.emptyByteArray{{/isUnitReturnType}}
  {{/mapResults}}
