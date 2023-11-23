  {{#mapResults}}
  def {{functionN}}{{firstParamsAndParens}}(in$: ReceiverInput): {{frameworkTypeArg}}[Array[Byte]] =
    {{#paramsRaw.isEmpty}}if in$.data.length>0 then throw new IllegalStateException(s"Expected empty array of bytes but got ${in$.data.length}"){{/paramsRaw.isEmpty}}
    {{^paramsRaw.isEmpty}}val params$ = {{functionN}}Deserializer(in$.data) {{/paramsRaw.isEmpty}}
    f$.{{functionN}}{{firstParamsCallAndParens}}({{#paramsRaw}}params$.{{name}}{{^last}}, {{/last}}{{/paramsRaw}}).map: r$=>
      {{^isUnitReturnType}}
      {{functionN}}ReturnTypeSerializer(r$)
      {{/isUnitReturnType}}
      {{#isUnitReturnType}}Array.emptyByteArray{{/isUnitReturnType}}
  {{/mapResults}}
