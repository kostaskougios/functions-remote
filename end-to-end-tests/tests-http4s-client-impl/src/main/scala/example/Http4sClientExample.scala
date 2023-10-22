package example

import cats.effect.{Async, IO, IOApp}
import endtoend.tests.cats.TestsCatsFunctionsCallerCirceJsonSerializedFactory
import fs2.Stream
import fs2.io.net.Network
import functions.model.{Coordinates, Serializer}
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*

object Http4sClientExample extends IOApp.Simple:
  val run = for {
    x <- EmberClientBuilder.default[IO].build.use { client =>
      val jsonClient = new FunctionsClient[IO](client, uri"http://localhost:8080")
      val jsonCaller = TestsCatsFunctionsCallerCirceJsonSerializedFactory.createCaller[IO](jsonClient.transportFunction)

      for r1 <- jsonCaller.catsAdd(5, 6)
      yield r1
    }
    _ = println(x)
  } yield ()

class FunctionsClient[F[_]: Async](client: Client[F], serverUri: Uri):
  private val dsl = Http4sClientDsl[F]
  import dsl.*

  protected def request(u: Uri, data: Array[Byte], contentType: `Content-Type`): Request[F] =
    PUT(u).withBodyStream(Stream.emits(data)).withContentType(contentType)

  protected def fullUri(clz: String, method: String, serializer: Serializer): Uri = serverUri / clz / method / serializer.toString

  def transportFunction(coordinates: String, data: Array[Byte]): F[Array[Byte]] =
    val Coordinates(clz, method, serializer) = coordinates
    val contentType                          = serializer match
      case Serializer.Json => `Content-Type`(MediaType.application.json)
      case Serializer.Avro => `Content-Type`(MediaType.application.`octet-stream`)

    val u = fullUri(clz, method, serializer)
    println(s"uri: $u")
    client.expect[Array[Byte]](request(u, data, contentType))
