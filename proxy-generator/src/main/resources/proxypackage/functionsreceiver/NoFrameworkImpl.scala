  {{^mapResults}}
  def {{functionN}}(in: ReceiverInput): Array[Byte] =
    {{#paramsRaw.isEmpty}}if in.data.length>0 then throw new IllegalStateException(s"Expected empty array of bytes but got ${in.data.length}"){{/paramsRaw.isEmpty}}
    {{^paramsRaw.isEmpty}}val params = {{functionN}}Deserializer(in.data) {{/paramsRaw.isEmpty}}
    {{^firstParamsRaw.isEmpty}}val paramsArgs = {{functionN}}ArgsDeserializer(in.argsData) {{/firstParamsRaw.isEmpty}}
    {{^isUnitReturnType}}
    val r = f.{{functionN}}{{^firstParamsRaw.isEmpty}}({{#firstParamsRaw}}paramsArgs.{{name}}{{^last}}, {{/last}}{{/firstParamsRaw}}){{/firstParamsRaw.isEmpty}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}ReturnTypeSerializer(r)
    {{/isUnitReturnType}}
    {{#isUnitReturnType}}
    f.{{functionN}}{{^firstParamsRaw.isEmpty}}({{#firstParamsRaw}}paramsArgs.{{name}}{{^last}}, {{/last}}{{/firstParamsRaw}}){{/firstParamsRaw.isEmpty}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    Array.emptyByteArray
    {{/isUnitReturnType}}

  def {{functionN}}WithFrameworkParams{{firstParamsAndParens}}(in: ReceiverInput): Array[Byte] =
    {{#paramsRaw.isEmpty}}if in.data.length>0 then throw new IllegalStateException(s"Expected empty array of bytes but got ${in.data.length}"){{/paramsRaw.isEmpty}}
    {{^paramsRaw.isEmpty}}val params = {{functionN}}Deserializer(in.data) {{/paramsRaw.isEmpty}}
    val r = f.{{functionN}}{{firstParamsCallAndParens}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{^isUnitReturnType}}
    {{functionN}}ReturnTypeSerializer(r)
    {{/isUnitReturnType}}
    {{#isUnitReturnType}}Array.emptyByteArray{{/isUnitReturnType}}

  {{/mapResults}}
