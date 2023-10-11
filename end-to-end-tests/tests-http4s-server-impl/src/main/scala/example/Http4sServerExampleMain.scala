package example

import cats.effect.{Async, IO, IOApp, Sync}
import com.comcast.ip4s.*
import endtoend.tests.{SimpleFunctions, SimpleFunctionsImpl}
import fs2.io.net.Network
import functions.http4s.RequestProcessor
import functions.receiver.FunctionsInvoker
import functions.receiver.model.RegisteredFunction
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.http4s.{HttpRoutes, Request, Response}

object Http4sServerExampleMain extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    val httpApp = simpleFunctionsRoutes[F].orNotFound

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

def simpleFunctionsRoutes[F[_]: Sync]: HttpRoutes[F] =
  val invoker          = FunctionsInvoker.withFunctions(RegisteredFunction[SimpleFunctions](new SimpleFunctionsImpl))
  val requestProcessor = new RequestProcessor[F](invoker)
  val dsl              = new Http4sDsl[F] {}
  import dsl.*

  HttpRoutes.of[F]:
    case req @ PUT -> Root / format / method => requestProcessor.invoke(req, format, method)
