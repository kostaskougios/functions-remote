package functions.helidon.requests

import functions.model.ReceiverInput
import io.helidon.webserver.http.{ServerRequest, ServerResponse}

class ResponseProcessor:
  def process(req: ServerRequest, res: ServerResponse)(f: ReceiverInput => Array[Byte]): Unit =
    val in     = if req.content().hasEntity then req.content().as(classOf[Array[Byte]]) else Array.emptyByteArray
    val result = f(ReceiverInput(in))
    res.send(result)

object ResponseProcessor:
  val Default = new ResponseProcessor
