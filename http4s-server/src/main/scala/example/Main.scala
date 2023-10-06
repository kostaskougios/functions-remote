package example

import cats.Applicative
import cats.effect.{Async, IO, IOApp, Sync}
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import io.circe.{Encoder, Json}
import org.http4s.{EntityEncoder, HttpRoutes}
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger

object Main extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    val helloWorldAlg = HelloWorld.impl[F]
    // Combine Service Routes into an HttpApp.
    // Can also be done via a Router if you
    // want to extract segments not checked
    // in the underlying routes.
    val httpApp       = (
      QuickstartRoutes.helloWorldRoutes[F](helloWorldAlg)
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
  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] { case GET -> Root / "hello" / name =>
      for {
        greeting <- H.hello(HelloWorld.Name(name))
        resp     <- Ok(greeting)
      } yield resp
    }

trait HelloWorld[F[_]]:
  def hello(n: HelloWorld.Name): F[HelloWorld.Greeting]

object HelloWorld:
  final case class Name(name: String) extends AnyVal

  final case class Greeting(greeting: String) extends AnyVal
  object Greeting:
    given Encoder[Greeting] =
      a =>
        Json.obj(
          ("message", Json.fromString(a.greeting))
        )

    given [F[_]]: EntityEncoder[F, Greeting] = jsonEncoderOf[F, Greeting]

  def impl[F[_]: Applicative]: HelloWorld[F] =
    n => Greeting("Hello, " + n.name).pure[F]
