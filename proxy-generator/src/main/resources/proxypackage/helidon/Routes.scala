package {{proxypackage}}

import functions.model.{Coordinates3, Serializer, ReceiverInput}
import io.helidon.webserver.http.{HttpRouting, ServerRequest, ServerResponse}

class {{className}}(
  receiver: {{exportedType.name}}Receiver{{exportedTypeTypeArgs}},
  serializer: Serializer
):
  def pathFor(coordinates: Coordinates3) = s"${coordinates.className}/${coordinates.method}/${coordinates.version}/${serializer.toString}"

  {{#functions}}
  private val {{functionN}}Path = pathFor({{methodParams}}.Methods.{{caseClassName}})
  def {{functionN}}Route(req: ServerRequest, res: ServerResponse): Unit =
    val in = req.content().as(classOf[Array[Byte]])
    val result = receiver.{{functionN}}(ReceiverInput(in))
    res.send(result)

  {{/functions}}
