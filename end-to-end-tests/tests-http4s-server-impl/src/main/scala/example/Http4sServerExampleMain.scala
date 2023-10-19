package example

import cats.effect.{Async, IO, IOApp}
import cats.syntax.all.*
import com.comcast.ip4s.*
import endtoend.tests.cats.{TestsCatsFunctionsImpl, TestsCatsFunctionsReceiver, TestsCatsFunctionsReceiverCirceJsonSerializedFactory}
import fs2.io.net.Network
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.http4s.{HttpRoutes, MediaType, Request, Response}

object Http4sServerExampleMain extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    val receiver = TestsCatsFunctionsReceiverCirceJsonSerializedFactory.createReceiver[F](new TestsCatsFunctionsImpl[F])
    val httpApp  = simpleFunctionsRoutes[F](receiver, `Content-Type`(MediaType.application.`octet-stream`)).orNotFound

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

def simpleFunctionsRoutes[F[_]: Async](receiver: TestsCatsFunctionsReceiver[F], mediaType: `Content-Type`): HttpRoutes[F] =
  val dsl = new Http4sDsl[F] {}
  import dsl.*

  HttpRoutes.of[F]:
    case req @ PUT -> Root / "catsAddR" =>
      val r = for
        inData <- req.body.compile.to(Array)
        res    <- receiver.catsAddR(inData)
      yield res
      Ok(r).map(_.withContentType(mediaType))
