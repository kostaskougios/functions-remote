package functions.http4s

import cats.effect.Concurrent
import fs2.Stream
import functions.model.{Coordinates, Serializer}
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.`Content-Type`

class Http4sTransport[F[_]: Concurrent](client: Client[F], serverUri: Uri):
  private val dsl = Http4sClientDsl[F]
  import dsl.*

  protected def request(u: Uri, data: Array[Byte], contentType: `Content-Type`): Request[F] =
    PUT(u).withBodyStream(Stream.emits(data)).withContentType(contentType)

  protected def fullUri(clz: String, method: String, serializer: Serializer): Uri = serverUri / clz / method / serializer.toString

  private val Json                                     = `Content-Type`(MediaType.application.json)
  private val OctetStream                              = `Content-Type`(MediaType.application.`octet-stream`)
  protected def contentTypeFor(serializer: Serializer) = serializer match
    case Serializer.Json => Json
    case Serializer.Avro => OctetStream

  def transportFunction(coordinates: String, data: Array[Byte]): F[Array[Byte]] =
    val Coordinates(clz, method, serializer) = coordinates
    val contentType                          = contentTypeFor(serializer)

    val u = fullUri(clz, method, serializer)
    client.expect[Array[Byte]](request(u, data, contentType))
