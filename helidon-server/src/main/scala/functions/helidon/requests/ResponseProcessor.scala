package functions.helidon.requests

import functions.model.Serializer.{Avro, Json}
import functions.model.{Coordinates4, ReceiverInput, Serializer}
import io.helidon.webserver.http.{ServerRequest, ServerResponse}

class ResponseProcessor(protected val serializer: Serializer):

  /** Override this to change default route paths, always start with a "/". Note this is called only once per route by the generated code during initialization.
    */
  def pathFor(coordinates: Coordinates4) = s"/${coordinates.className}/${coordinates.method}/${coordinates.version}/${coordinates.serializer}"

  /** Override this to provide custom headers
    */
  def responseHeaders(req: ServerRequest, res: ServerResponse): Unit =
    serializer match
      case Avro => res.header("Content-Type", "application/avro")
      case Json => res.header("Content-Type", "application/json")

  /** called by the generated code to process the request and send the response
    */
  def process(req: ServerRequest, res: ServerResponse)(f: ReceiverInput => Array[Byte]): Unit =
    val in     = if req.content().hasEntity then req.content().as(classOf[Array[Byte]]) else Array.emptyByteArray
    val result = f(ReceiverInput(in))
    responseHeaders(req, res)
    res.send(result)
