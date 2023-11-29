package functions.http4s

import cats.effect.Async
import cats.syntax.all.*
import functions.model.{Coordinates3, ReceiverInput, Serializer}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{MediaType, Request, Response}

class Http4sRoutes[F[_]: Async](serializer: Serializer):
  private val dsl = Http4sDsl[F]
  import dsl.*

  // override this if you want to manually choose content type
  protected def contentType: `Content-Type` =
    serializer match
      case Serializer.Json => `Content-Type`(MediaType.application.json)
      case Serializer.Avro => `Content-Type`(MediaType.application.`octet-stream`)

  // Override this if you want to change the paths
  def pathFor(coordinates: Coordinates3): Path = Root / coordinates.className / coordinates.method / coordinates.version / serializer.toString

  def routeFor(req: Request[F], f: ReceiverInput => F[Array[Byte]]): F[Response[F]] =
    val r = for
      inData <- req.body.compile.to(Array)
      res    <- f(ReceiverInput(inData))
    yield res
    Ok(r).map(_.withContentType(contentType))
