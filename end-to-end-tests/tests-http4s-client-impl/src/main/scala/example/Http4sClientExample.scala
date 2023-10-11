package example

import cats.effect.{Concurrent, IO, IOApp}
import endtoend.tests.{SimpleFunctionsCirceJsonSerializer, SimpleFunctionsMethods}
import fs2.io.net.Network
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*
import fs2.Stream
import cats.syntax.all.*

object Http4sClientExample extends IOApp.Simple:
  val run = for {
    result <- EmberClientBuilder.default[IO].build.use { client =>
      doRequest[IO](client)
    }
    _ = println(result)
  } yield ()

  def doRequest[F[_]: Concurrent](client: Client[F]): F[Int] =
    val serializer = new SimpleFunctionsCirceJsonSerializer
    val dsl        = new Http4sClientDsl[F] {}

    import dsl.*
    for res <- client.expect[Array[Byte]](
        POST(uri"http://localhost:8080/Json/endtoend.tests.SimpleFunctions:add").withBodyStream(
          Stream.emits(
            serializer.addSerializer(SimpleFunctionsMethods.Add(2, 3))
          )
        )
      )
    yield serializer.addReturnTypeDeserializer(res)
