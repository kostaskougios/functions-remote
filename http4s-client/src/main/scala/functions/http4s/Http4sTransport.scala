package functions.http4s

import cats.effect.Concurrent
import fs2.Stream
import functions.model.{Coordinates4, Serializer, TransportInput}
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

  protected def baseUrlOf(coordinates: Coordinates4) =
    serverUri / coordinates.className / coordinates.method / coordinates.version / coordinates.serializer.toString

  protected def fullUri(input: TransportInput): Uri =
    val coordinates = input.coordinates4
    val baseUrl     = baseUrlOf(coordinates)
    input.args.foldLeft(baseUrl)((u, arg) => u / arg.toString)

  protected def contentType(serializer: Serializer) = serializer match
    case Serializer.Json => `Content-Type`(MediaType.application.json)
    case Serializer.Avro => `Content-Type`(MediaType.application.`octet-stream`)

  def transportFunction(in: TransportInput): F[Array[Byte]] =
    val u = fullUri(in)
    client.expect[Array[Byte]](request(u, in.data, contentType(in.coordinates4.serializer)))
