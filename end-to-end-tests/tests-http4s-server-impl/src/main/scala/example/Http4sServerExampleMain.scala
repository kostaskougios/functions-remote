package example

import cats.effect.{Async, IO, IOApp}
import com.comcast.ip4s.*
import endtoend.tests.cats.TestsCatsFunctionsReceiverFactory
import fs2.io.net.Network
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import tests.cats.TestsCatsFunctionsImpl

object Http4sServerExampleMain extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    val impl           = new TestsCatsFunctionsImpl[F]
    val testRoutesJson = TestsCatsFunctionsReceiverFactory.newJsonTestsCatsFunctionsRoutes(impl)
    val testRoutesAvro = TestsCatsFunctionsReceiverFactory.newAvroTestsCatsFunctionsRoutes(impl)
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
