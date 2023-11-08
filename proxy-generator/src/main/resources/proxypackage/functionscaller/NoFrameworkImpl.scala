  {{^exportedType.hasFramework}}
  def {{functionN}}{{firstParamsAndParens}}({{params}}): {{resultN}} =
    {{^paramsRaw.isEmpty}}val c  = {{caseClass}}({{paramsCall}}){{/paramsRaw.isEmpty}}
    val binIn = {{#paramsRaw.isEmpty}}Array.emptyByteArray{{/paramsRaw.isEmpty}}{{^paramsRaw.isEmpty}}{{functionN}}ToByteArray(c){{/paramsRaw.isEmpty}}
    val trIn = StdTransportInput({{methodParams}}.Methods.{{caseClassName}}.withSerializer(serializer), binIn, {{firstParamsAsArray}})

    val binOut = transport(trIn)
    {{^isUnitReturnType}}{{functionN}}ReturnTypeFromByteArray(binOut){{/isUnitReturnType}}
    {{#isUnitReturnType}}(){{/isUnitReturnType}}

  {{/exportedType.hasFramework}}
