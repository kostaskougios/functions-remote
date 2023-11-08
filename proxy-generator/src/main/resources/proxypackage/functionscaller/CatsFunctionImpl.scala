    {{#exportedType.hasFramework}}
    transport(trIn).map: binOut=>
      {{^isUnitReturnType}}{{functionN}}ReturnTypeFromByteArray(binOut){{/isUnitReturnType}}
      {{#isUnitReturnType}}(){{/isUnitReturnType}}
    {{/exportedType.hasFramework}}
