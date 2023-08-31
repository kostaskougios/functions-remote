package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}


trait {{methodParams}}

object {{methodParams}}:

  {{#caseClasses}}
  case class {{caseClass}}({{paramsDecl}}) extends {{methodParams}}
  {{/caseClasses}}

  enum Methods:
    {{#caseClasses}}
    case {{caseClass}}
    {{/caseClasses}}
