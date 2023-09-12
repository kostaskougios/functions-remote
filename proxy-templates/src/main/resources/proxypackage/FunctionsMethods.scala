package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}


trait {{className}}

object {{className}}:

  {{#functions}}
  case class {{caseClassName}}({{params}}) extends {{className}}
  {{/functions}}

  enum Methods:
    {{#functions}}
    case {{caseClassName}}
    {{/functions}}
