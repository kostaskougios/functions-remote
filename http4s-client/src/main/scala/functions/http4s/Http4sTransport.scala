package functions.http4s

import cats.effect.Concurrent
import fs2.Stream
import functions.model.{Coordinates2, Serializer}
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.`Content-Type`

class Http4sTransport[F[_]: Concurrent](client: Client[F], serverUri: Uri, serializer: Serializer):
  private val dsl = Http4sClientDsl[F]
  import dsl.*

  protected def request(u: Uri, data: Array[Byte], contentType: `Content-Type`): Request[F] =
    PUT(u).withBodyStream(Stream.emits(data)).withContentType(contentType)

  protected def fullUri(coordinates: Coordinates2): Uri = serverUri / coordinates.className / coordinates.method / serializer.toString

  private val contentType = serializer match
    case Serializer.Json => `Content-Type`(MediaType.application.json)
    case Serializer.Avro => `Content-Type`(MediaType.application.`octet-stream`)

  def transportFunction(coordinates: Coordinates2, data: Array[Byte]): F[Array[Byte]] =
    val u = fullUri(coordinates)
    client.expect[Array[Byte]](request(u, data, contentType))
