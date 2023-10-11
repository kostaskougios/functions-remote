package functions.http4s

import cats.effect.Sync
import functions.receiver.FunctionsInvoker
import org.http4s.{MediaType, Request, Response}
import cats.syntax.all.*
import functions.Log
import functions.model.Serializer
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`

class RequestProcessor[F[_]: Sync](invoker: FunctionsInvoker):
  private val dsl = new Http4sDsl[F] {}
  import dsl.*

  def invoke(req: Request[F], format: String, method: String): F[Response[F]] =
    val serializer  = Serializer.valueOf(format)
    val contentType = toContentType(serializer)
    val coordinates = method + ":" + format
    for
      b <- req.body.compile.to(Array)
      _      = Log.info(s"Invoking coordinates $coordinates")
      resBin = invoker.invoke(coordinates, b)
      r <- Ok(resBin).map(_.withContentType(contentType))
    yield r

  private def toContentType(serializer: Serializer) =
    serializer match
      case Serializer.Json => `Content-Type`(MediaType.application.json)
      case Serializer.Avro => `Content-Type`(MediaType.application.`octet-stream`)
      case x               => throw new IllegalArgumentException(s"Not yet implemented content type for $x , please report as a bug")
