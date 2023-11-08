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
{{{CatsFunctionImpl}}}{{{NoFrameworkImpl}}}
  {{/functions}}
