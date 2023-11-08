    {{^exportedType.hasFramework}}
    val binOut = transport(trIn)
    {{^isUnitReturnType}}{{functionN}}ReturnTypeFromByteArray(binOut){{/isUnitReturnType}}
    {{#isUnitReturnType}}(){{/isUnitReturnType}}

    {{/exportedType.hasFramework}}
