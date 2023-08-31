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
    // foreach caseClasses
    case `caseClass`
  // end caseClasses
