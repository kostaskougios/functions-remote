package tests.cats

import cats.effect.Async
import com.comcast.ip4s.*
import endtoend.tests.cats.TestsCatsFunctionsReceiverFactory
import fs2.io.net.Network
import org.http4s.{HttpRoutes, Request, Response, Status}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.{ErrorAction, Logger}
import cats.syntax.all.*

object Server:
  def newServer[F[_]: Async: Network](port: Port) =
    val impl           = new TestsCatsFunctionsImpl
    val testRoutesJson = TestsCatsFunctionsReceiverFactory.newJsonTestsCatsFunctionsRoutes(impl)
    val testRoutesAvro = TestsCatsFunctionsReceiverFactory.newAvroTestsCatsFunctionsRoutes(impl)
    val routes         = HttpRoutes.of(testRoutesJson.allRoutes orElse testRoutesAvro.allRoutes)

    def errorHandler(r: Request[F], t: Throwable) =
      t match {
        case _: ExpectedException => // ignore, we expect this to be thrown
        case x                    => x.printStackTrace()
      }
      ().pure

    val routesWithErrorHandling = ErrorAction.httpRoutes(routes, errorHandler)
    val finalHttpApp            = Logger.httpApp(true, true)(routesWithErrorHandling.orNotFound)

    EmberServerBuilder.default
      .withHost(ipv4"0.0.0.0")
      .withPort(port)
      .withHttpApp(finalHttpApp)
      .build
