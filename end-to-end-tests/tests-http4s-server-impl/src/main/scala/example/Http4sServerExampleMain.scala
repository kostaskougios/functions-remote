package example

import cats.effect.{Async, IO, IOApp, Sync}
import cats.syntax.all.*
import com.comcast.ip4s.*
import endtoend.tests.SimpleFunctionsMethods
import fs2.io.net.Network
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.http4s.{EntityEncoder, HttpRoutes, MediaType}

object Http4sServerExampleMain extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    // Combine Service Routes into an HttpApp.
    // Can also be done via a Router if you
    // want to extract segments not checked
    // in the underlying routes.
    val httpApp = (
      QuickstartRoutes.helloWorldRoutes[F]
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
  def helloWorldRoutes[F[_]: Sync]: HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] { case req @ POST -> Root / "Json" / SimpleFunctionsMethods.Methods.Add =>
      req.bodyText
      for
        b <- req.body.compile.to(Array)
        r <- Ok(s"""{"x":5}""".getBytes("UTF-8")).map(_.withContentType(`Content-Type`(MediaType.application.json)))
      yield r
    }
