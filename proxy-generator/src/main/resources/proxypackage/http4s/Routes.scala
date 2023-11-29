package {{proxypackage}}

import functions.model.{Coordinates3, Serializer, ReceiverInput}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{MediaType, Request, Response}
import cats.syntax.all.*
import functions.http4s.Http4sRoutes
{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

class {{className}}{{frameworkTypeArgFull}}(
  receiver: {{exportedType.name}}Receiver{{exportedTypeTypeArgs}},
  http4sRoutes: Http4sRoutes{{exportedTypeTypeArgs}}
):
  private val dsl = Http4sDsl{{exportedTypeTypeArgs}}
  import dsl.*

  val allRoutes: PartialFunction[Request{{exportedTypeTypeArgs}}, {{frameworkTypeArg}}[Response{{exportedTypeTypeArgs}}]] = {{#functions}}{{functionN}} {{^last}}orElse{{/last}} {{/functions}}

  {{#functions}}
  private val {{functionN}}Path = http4sRoutes.pathFor({{methodParams}}.Methods.{{caseClassName}})
  def {{functionN}}: PartialFunction[Request{{exportedTypeTypeArgs}}, {{frameworkTypeArg}}[Response{{exportedTypeTypeArgs}}]] =
    case req @ {{extras.httpMethod}} -> `{{functionN}}Path` {{#extras.hasHttpArgs}} / {{extras.urlArgs}} {{/extras.hasHttpArgs}} => http4sRoutes.routeFor(req, receiver.{{functionN}}{{firstParamsCallAndParens}})

  {{/functions}}
