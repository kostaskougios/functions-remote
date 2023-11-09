package {{proxypackage}}

import functions.model.FunctionsMethods
import functions.model.Coordinates3
{{#imports}}
import {{.}}
{{/imports}}


trait {{className}}

object {{className}} extends FunctionsMethods:

  {{#functions}}
  {{^firstParamsRaw.isEmpty}}
  case class {{caseClassName}}Args({{firstParams}}) extends {{className}}
  {{/firstParamsRaw.isEmpty}}
  case class {{caseClassName}}({{params}}) extends {{className}}
  {{/functions}}

  // Make sure you generate the BuildInfo class i.e. via sbt build-info plugin as described in the functions-remote docs.
  override def artifactCoordinates = s"${BuildInfo.organization}:${BuildInfo.exportedArtifact}:${BuildInfo.version}"
  override def version: String = BuildInfo.version

  val AllMethods = List({{#functions}}{{caseClassName}}{{^last}}, {{/last}}{{/functions}})
  object Methods:
    {{#functions}}
    val {{caseClassName}} = Coordinates3("{{proxypackage}}.{{exportedType.name}}", "{{functionN}}", version, Map({{{extras.coordinatesPropertiesValues}}}))
    {{/functions}}
