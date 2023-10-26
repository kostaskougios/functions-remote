package endtoend.tests.cats

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{Async, IO}
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import functions.model.Serializer
import org.http4s.HttpRoutes
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.scalatest.AsyncTestSuite
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers.*
import tests.cats.TestsCatsFunctionsImpl

class EndToEndHttp4sRoutesSpec extends AsyncFreeSpec with AsyncTestSuite with AsyncIOSpec:
  val serverUri                    = uri"http://localhost:7500"
  def server[F[_]: Async: Network] =
    val impl           = new TestsCatsFunctionsImpl
    val testRoutesJson = TestsCatsFunctionsReceiverFactory.newJsonTestsCatsFunctionsRoutes(impl)
    val testRoutesAvro = TestsCatsFunctionsReceiverFactory.newAvroTestsCatsFunctionsRoutes(impl)
    val routes         = HttpRoutes.of(testRoutesJson.allRoutes orElse testRoutesAvro.allRoutes)
    val finalHttpApp   = Logger.httpApp(true, true)(routes.orNotFound)

    EmberServerBuilder.default
      .withHost(ipv4"0.0.0.0")
      .withPort(port"7500")
      .withHttpApp(finalHttpApp)
      .build

  def client[F[_]: Async: Network] =
    EmberClientBuilder
      .default[F]
      .build

  "TestsCatsFunctions" - {
    for (serializer, functions) <- Seq(
        (Serializer.Avro, TestsCatsFunctionsCallerFactory.newHttp4sAvroTestsCatsFunctions[IO](_, serverUri)),
        (Serializer.Json, TestsCatsFunctionsCallerFactory.newHttp4sJsonTestsCatsFunctions[IO](_, serverUri))
      )
    do
      s"$serializer: catsAdd" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val avroCaller = functions(client)
          for r <- avroCaller.catsAdd(1, 2) yield r should be(3)
      }
  }
