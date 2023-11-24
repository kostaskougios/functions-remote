package {{proxypackage}}

import functions.model.{Coordinates3, Serializer, ReceiverInput}
import io.helidon.webserver.http.{HttpRouting, ServerRequest, ServerResponse}
import functions.helidon.requests.*

class {{className}}(
  receiver: {{exportedType.name}}Receiver{{exportedTypeTypeArgs}},
  serializer: Serializer,
  responseProcessor: ResponseProcessor
):
  def routes(routing: HttpRouting.Builder):Unit =
    {{#functions}}
    {{functionN}}Route(routing)
    {{/functions}}

  {{#functions}}
  private val {{functionN}}Path = responseProcessor.pathFor({{methodParams}}.Methods.{{caseClassName}}.withSerializer(serializer))

  def {{functionN}}Route(routing: HttpRouting.Builder): Unit =
    routing.{{extras.httpMethod}}({{functionN}}Path + "{{extras.pathParams}}", {{functionN}}RequestResponse)

  def {{functionN}}RequestResponse(req: ServerRequest, res: ServerResponse): Unit =
    {{#extras.hasHttpArgs}}
    val httpArgs = new HelidonParams(req.path.pathParameters)
    {{/extras.hasHttpArgs}}
    {{#extras.httpArgs}}
    val {{name}} = httpArgs.get[{{tpe}}]("{{name}}")
    {{/extras.httpArgs}}
    responseProcessor.process(req,res)(receiver.{{functionN}}WithFrameworkParams{{firstParamsCallAndParens}})

  {{/functions}}
