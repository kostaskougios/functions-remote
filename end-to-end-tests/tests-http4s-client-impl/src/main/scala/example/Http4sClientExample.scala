package example

import cats.effect.{Async, Concurrent, IO, IOApp}
import endtoend.tests.cats.{TestsCatsFunctionsCallerAvroSerializedFactory, TestsCatsFunctionsCallerCirceJsonSerializedFactory}
import fs2.Stream
import fs2.io.net.Network
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
      for
        r1 <- doJsonRequest[IO](client)
        r2 <- doAvroRequest[IO](client)
      yield (r1, r2)
    }
    _ = println(x)
  } yield ()

  def transportFunction[F[_]: Concurrent](client: Client[F], contentType: `Content-Type`, coordinates: String, data: Array[Byte]): F[Array[Byte]] =
    val dsl = new Http4sClientDsl[F] {}
    import dsl.*

    val Array(clz, method, serializer) = coordinates.split(":")
    val u                              = uri"http://localhost:8080" / clz / method / serializer
    println(s"uri: $u")
    client.expect[Array[Byte]](
      PUT(u).withBodyStream(Stream.emits(data)).withContentType(contentType)
    )

  def doJsonRequest[F[_]: Async](client: Client[F]) =
    val caller =
      TestsCatsFunctionsCallerCirceJsonSerializedFactory.createCaller[F](transportFunction[F](client, `Content-Type`(MediaType.application.json), _, _))
    caller.catsAddR(5, 6)

  def doAvroRequest[F[_]: Async](client: Client[F]) =
    val caller =
      TestsCatsFunctionsCallerAvroSerializedFactory.createCaller[F](transportFunction[F](client, `Content-Type`(MediaType.application.`octet-stream`), _, _))
    caller.catsAddLR(10, 5)
