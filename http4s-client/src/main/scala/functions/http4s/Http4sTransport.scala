package functions.http4s

import cats.effect.Concurrent
import fs2.Stream
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits.uri

class Http4sTransport[F[_]: Concurrent](client: Client[F]):
  private val dsl = new Http4sClientDsl[F] {}
  import dsl.*

  def transportFunction(coordinates: String, data: Array[Byte]): F[Array[Byte]] =
    client.expect[Array[Byte]](
      PUT(uri"http://localhost:8080/Json/endtoend.tests.SimpleFunctions:add").withBodyStream(
        Stream.emits(data)
      )
    )
