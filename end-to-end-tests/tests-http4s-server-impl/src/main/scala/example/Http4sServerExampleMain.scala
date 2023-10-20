package example

import cats.effect.{Async, IO, IOApp}
import com.comcast.ip4s.*
import endtoend.tests.cats.{TestsCatsFunctionsHttp4sRoutes, TestsCatsFunctionsImpl, TestsCatsFunctionsReceiverCirceJsonSerializedFactory}
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
    val receiver   = TestsCatsFunctionsReceiverCirceJsonSerializedFactory.createReceiver[F](new TestsCatsFunctionsImpl[F])
    val testRoutes = new TestsCatsFunctionsHttp4sRoutes[F](receiver, `Content-Type`(MediaType.application.`octet-stream`))
    val routes     = HttpRoutes.of[F](testRoutes.allRoutes)
    val httpApp    = routes.orNotFound

    // With Middlewares in place
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
