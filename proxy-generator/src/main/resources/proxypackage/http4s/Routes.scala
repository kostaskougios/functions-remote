package {{proxypackage}}

import functions.model.{Coordinates3, Serializer, ReceiverInput}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{MediaType, Request, Response}
import cats.syntax.all.*

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

class {{className}}{{frameworkTypeArgFull}}(
  receiver: {{exportedType.name}}Receiver{{exportedTypeTypeArgs}},
  serializer: Serializer
):
  private val dsl = Http4sDsl{{exportedTypeTypeArgs}}
  import dsl.*

  // override this if you want to manually choose content type
  def contentType: `Content-Type` =
    serializer match
      case Serializer.Json => `Content-Type`(MediaType.application.json)
      case Serializer.Avro => `Content-Type`(MediaType.application.`octet-stream`)
  // Override this if you want to change the paths
  def pathFor(coordinates: Coordinates3) = Root / coordinates.className / coordinates.method / coordinates.version / serializer.toString

  val allRoutes: PartialFunction[Request{{exportedTypeTypeArgs}}, {{frameworkTypeArg}}[Response{{exportedTypeTypeArgs}}]] = {{#functions}}{{functionN}} {{^last}}orElse{{/last}} {{/functions}}

  private def routeFor(req: Request[{{frameworkTypeArg}}], f: ReceiverInput => {{frameworkTypeArg}}[Array[Byte]]) =
    val r = for
      inData <- req.body.compile.to(Array)
      res <- f(ReceiverInput(inData))
    yield res
    Ok(r).map(_.withContentType(contentType))

  {{#functions}}
  private val {{functionN}}Path = pathFor({{methodParams}}.Methods.{{caseClassName}})
  def {{functionN}}: PartialFunction[Request{{exportedTypeTypeArgs}}, {{frameworkTypeArg}}[Response{{exportedTypeTypeArgs}}]] =
    case req @ {{extras.httpMethod}} -> `{{functionN}}Path` {{#extras.hasHttpArgs}} / {{extras.urlArgs}} {{/extras.hasHttpArgs}} => routeFor(req, receiver.{{functionN}}{{firstParamsCallAndParens}})


  {{/functions}}
