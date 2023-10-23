package {{proxypackage}}

import functions.model.FunctionsMethods
import functions.model.Coordinates2
{{#imports}}
import {{.}}
{{/imports}}


trait {{className}}

object {{className}} extends FunctionsMethods:

  {{#functions}}
  case class {{caseClassName}}({{params}}) extends {{className}}
  {{/functions}}

  // Make sure you generate the BuildInfo class i.e. via sbt build-info plugin as described in the functions-remote docs.
  override def artifactCoordinates = s"${BuildInfo.organization}:${BuildInfo.exportedArtifact}:${BuildInfo.version}"

  val AllMethods = List({{#functions}}{{caseClassName}}{{^last}}, {{/last}}{{/functions}})
  object Methods:
    {{#functions}}
    val {{caseClassName}} = Coordinates2("{{proxypackage}}.{{exportedType.name}}","{{functionN}}")
    {{/functions}}
