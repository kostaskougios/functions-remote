package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}


trait {{className}}

object {{className}}:

  {{#functions}}
  case class {{caseClassName}}({{params}}) extends {{className}}
  {{/functions}}

  val AllMethods = List({{#functions}}{{caseClassName}}{{^last}}, {{/last}}{{/functions}})
  object Methods:
    {{#functions}}
    val {{caseClassName}} = "{{proxypackage}}.{{exportedType.name}}.{{functionN}}:{{config.apiVersion}}"
    {{/functions}}
