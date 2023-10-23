package example

import cats.effect.*
import endtoend.tests.cats.{TestsCatsFunctionsCallerAvroSerializedFactory, TestsCatsFunctionsCallerCirceJsonSerializedFactory}
import fs2.io.net.Network
import functions.http4s.Http4sTransport
import org.http4s.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*
import cats.syntax.all.*

object Http4sClientExample extends IOApp.Simple:
  val run = for {
    x <- EmberClientBuilder.default[IO].build.use { client =>
      val transport  = new Http4sTransport[IO](client, uri"http://localhost:8080")
      val jsonCaller = TestsCatsFunctionsCallerCirceJsonSerializedFactory.createCaller[IO](transport.transportFunction)
      val avroCaller = TestsCatsFunctionsCallerAvroSerializedFactory.createCaller[IO](transport.transportFunction)

      val ios =
        for i <- 1 to 100
        yield for
          r1 <- jsonCaller.catsAdd(5 + i, 6)
          r2 <- avroCaller.catsAdd(10 + i, 20)
        yield (r1, r2)

      ios.toList.sequence
    }
    _ = println(x)
  } yield ()
