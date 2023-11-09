  {{^exportedType.hasFramework}}
  def {{functionN}}{{firstParamsAndParens}}({{params}}): {{resultN}} =
    {{^paramsRaw.isEmpty}}val c  = {{caseClass}}({{paramsCall}}){{/paramsRaw.isEmpty}}
    val binIn = {{#paramsRaw.isEmpty}}Array.emptyByteArray{{/paramsRaw.isEmpty}}{{^paramsRaw.isEmpty}}{{functionN}}ToByteArray(c){{/paramsRaw.isEmpty}}
    val trIn = StdTransportInput(
      {{methodParams}}.Methods.{{caseClassName}}.withSerializer(serializer),
      binIn,
      {{firstParamsAsArray}},
      {{#firstParamsRaw.isEmpty}}Array.emptyByteArray{{/firstParamsRaw.isEmpty}}
      {{^firstParamsRaw.isEmpty}}
      if serializeArgs then {{functionN}}ArgsToByteArray({{caseClass}}Args({{firstParamsCall}}))
      else Array.emptyByteArray
      {{/firstParamsRaw.isEmpty}}
    )

    val binOut = transport(trIn)
    {{^isUnitReturnType}}{{functionN}}ReturnTypeFromByteArray(binOut){{/isUnitReturnType}}
    {{#isUnitReturnType}}(){{/isUnitReturnType}}

  {{/exportedType.hasFramework}}
