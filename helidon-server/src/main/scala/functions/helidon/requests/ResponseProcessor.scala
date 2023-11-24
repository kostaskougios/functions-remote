package functions.helidon.requests

import functions.model.Serializer.{Avro, Json}
import functions.model.{Coordinates4, ReceiverInput, Serializer}
import io.helidon.webserver.http.{ServerRequest, ServerResponse}

class ResponseProcessor(protected val serializer: Serializer):

  // Override this to change default route paths, always start with a /.
  // Note this is called only once per route during initialization
  def pathFor(coordinates: Coordinates4) = s"/${coordinates.className}/${coordinates.method}/${coordinates.version}/${coordinates.serializer}"

  def responseHeaders(req: ServerRequest, res: ServerResponse): Unit =
    serializer match
      case Avro => res.header("Content-Type", "application/avro")
      case Json => res.header("Content-Type", "application/json")

  def process(req: ServerRequest, res: ServerResponse)(f: ReceiverInput => Array[Byte]): Unit =
    val in     = if req.content().hasEntity then req.content().as(classOf[Array[Byte]]) else Array.emptyByteArray
    val result = f(ReceiverInput(in))
    responseHeaders(req, res)
    res.send(result)
