  {{^mapResults}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    {{#paramsRaw.isEmpty}}if data.length>0 then throw new IllegalStateException(s"Expected empty array of bytes but got ${data.length}"){{/paramsRaw.isEmpty}}
    {{^paramsRaw.isEmpty}}val params = {{functionN}}Deserializer(data) {{/paramsRaw.isEmpty}}
    {{^isUnitReturnType}}
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}ReturnTypeSerializer(r)
    {{/isUnitReturnType}}
    {{#isUnitReturnType}}
    f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    Array.emptyByteArray
    {{/isUnitReturnType}}
  {{/mapResults}}
