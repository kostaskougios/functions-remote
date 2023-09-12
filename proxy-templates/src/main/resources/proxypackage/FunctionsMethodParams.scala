package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}


trait {{functionsProxy}}

object {{functionsProxy}}:

  {{#functions}}
  case class {{caseClassName}}({{params}}) extends {{functionsProxy}}
  {{/functions}}

  enum Methods:
    {{#functions}}
    case {{caseClassName}}
    {{/functions}}
