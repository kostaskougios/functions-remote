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
    // Make sure you create the BuildInfo class i.e. via sbt build-info plugin as described in the functions-remote docs.
    val {{caseClassName}} = s"${BuildInfo.organization}:${BuildInfo.exportedArtifact}:{{proxypackage}}.{{exportedType.name}}.{{functionN}}:{{config.apiVersion}}"
    {{/functions}}
