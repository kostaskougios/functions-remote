package {{proxypackage}}

import functions.model.{Coordinates3, Serializer, ReceiverInput}
import io.helidon.webserver.http.{HttpRouting, ServerRequest, ServerResponse}
import functions.helidon.requests.HelidonParams

class {{className}}(
  receiver: {{exportedType.name}}Receiver{{exportedTypeTypeArgs}},
  serializer: Serializer
):
  // override this to change default route paths, always start with a /
  def pathFor(coordinates: Coordinates3) = s"/${coordinates.className}/${coordinates.method}/${coordinates.version}/${serializer.toString}"

  def routes(routing: HttpRouting.Builder):Unit =
    {{#functions}}
    {{functionN}}Route(routing)
    {{/functions}}

  {{#functions}}
  private val {{functionN}}Path = pathFor({{methodParams}}.Methods.{{caseClassName}})

  def {{functionN}}Route(routing: HttpRouting.Builder): Unit =
    routing.{{extras.httpMethod}}({{functionN}}Path + "{{extras.pathParams}}", {{functionN}}RequestResponse)

  def {{functionN}}RequestResponse(req: ServerRequest, res: ServerResponse): Unit =
    val in = if req.content().hasEntity then req.content().as(classOf[Array[Byte]]) else Array.emptyByteArray
    {{#extras.hasHttpArgs}}
    val httpArgs = new HelidonParams(req.path.pathParameters)
    {{/extras.hasHttpArgs}}
    {{#extras.httpArgs}}
    val {{name}} = httpArgs.get[{{tpe}}]("{{name}}")
    {{/extras.httpArgs}}
    val result = receiver.{{functionN}}WithFrameworkParams{{firstParamsCallAndParens}}(ReceiverInput(in))
    res.send(result)

  {{/functions}}
