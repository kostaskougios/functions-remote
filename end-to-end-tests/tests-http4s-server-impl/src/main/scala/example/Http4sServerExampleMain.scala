package example

import cats.effect.{Async, IO, IOApp, Sync}
import cats.syntax.all.*
import com.comcast.ip4s.*
import endtoend.tests.{SimpleFunctionsImpl, SimpleFunctionsMethods, SimpleFunctionsReceiver, SimpleFunctionsReceiverCirceJsonSerializedFactory}
import fs2.io.net.Network
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.http4s.{EntityEncoder, HttpRoutes, MediaType, Request, Response}

object Http4sServerExampleMain extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    // Combine Service Routes into an HttpApp.
    // Can also be done via a Router if you
    // want to extract segments not checked
    // in the underlying routes.
    val impl     = new SimpleFunctionsImpl
    val receiver = SimpleFunctionsReceiverCirceJsonSerializedFactory.createReceiver(impl)
    val httpApp  = (
      QuickstartRoutes.helloWorldRoutes[F](receiver, `Content-Type`(MediaType.application.json))
    ).orNotFound

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

object QuickstartRoutes:
  def helloWorldRoutes[F[_]: Sync](receiver: SimpleFunctionsReceiver, contentType: `Content-Type`): HttpRoutes[F] =
    val routes = new SFRoutes[F](receiver, contentType)
    HttpRoutes.of[F](routes.addRoute)

class SFRoutes[F[_]: Sync](receiver: SimpleFunctionsReceiver, contentType: `Content-Type`):
  private val dsl = new Http4sDsl[F] {}
  import dsl.*

  def addRoute: PartialFunction[Request[F], F[Response[F]]] =
    case req @ PUT -> Root / "Json" / method =>
      for
        b <- req.body.compile.to(Array)
        _      = println(s"Received : ${new String(b, "UTF-8")}")
        resBin = receiver.invoke(method, b)
        r <- Ok(resBin).map(_.withContentType(contentType))
      yield r
