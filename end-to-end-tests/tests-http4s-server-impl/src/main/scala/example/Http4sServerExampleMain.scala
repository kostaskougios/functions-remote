package example

import cats.effect.{Async, IO, IOApp}
import com.comcast.ip4s.*
import endtoend.tests.cats.{
  TestsCatsFunctionsHttp4sRoutes,
  TestsCatsFunctionsImpl,
  TestsCatsFunctionsReceiverAvroSerializedFactory,
  TestsCatsFunctionsReceiverCirceJsonSerializedFactory
}
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.http4s.{HttpRoutes, MediaType}

object Http4sServerExampleMain extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    val impl           = new TestsCatsFunctionsImpl[F]
    val jsonReceiver   = TestsCatsFunctionsReceiverCirceJsonSerializedFactory.createReceiver[F](impl)
    val avroReceiver   = TestsCatsFunctionsReceiverAvroSerializedFactory.createReceiver[F](impl)
    val testRoutesJson = new TestsCatsFunctionsHttp4sRoutes[F](jsonReceiver, `Content-Type`(MediaType.application.json))
    val testRoutesAvro = new TestsCatsFunctionsHttp4sRoutes[F](avroReceiver, `Content-Type`(MediaType.application.`octet-stream`), "Avro")
    val routes         = HttpRoutes.of[F](testRoutesJson.allRoutes orElse testRoutesAvro.allRoutes)
    val httpApp        = routes.orNotFound

    val finalHttpApp = Logger.httpApp(true, true)(httpApp)
    for {
      _ <-
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
