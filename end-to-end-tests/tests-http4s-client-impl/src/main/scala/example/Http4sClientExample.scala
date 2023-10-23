package example

import cats.effect.*
import cats.syntax.all.*
import endtoend.tests.cats.{TestsCatsFunctionsCallerAvroSerializedFactory, TestsCatsFunctionsCallerCirceJsonSerializedFactory}
import fs2.io.net.Network
import functions.http4s.Http4sTransport
import functions.model.Serializer
import functions.model.Serializer.Json
import org.http4s.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*

object Http4sClientExample extends IOApp.Simple:
  val run = QuickstartClient.run[IO]

object QuickstartClient:
  def run[F[_]: Async: Network]: F[Unit] =
    for
      x <- EmberClientBuilder
        .default[F]
        .build
        .use: client =>
          val transportJson = new Http4sTransport[F](client, uri"http://localhost:8080", Serializer.Json)
          val transportAvro = new Http4sTransport[F](client, uri"http://localhost:8080", Serializer.Avro)
          val jsonCaller    = TestsCatsFunctionsCallerCirceJsonSerializedFactory.createCaller[F](transportJson.transportFunction)
          val avroCaller    = TestsCatsFunctionsCallerAvroSerializedFactory.createCaller[F](transportAvro.transportFunction)

          val ios =
            for i <- 1 to 10
            yield for
              r1 <- jsonCaller.catsAdd(5 + i, 6)
              r2 <- avroCaller.catsAdd(10 + i, 20)
            yield
              if r1 != 11 + i then throw new IllegalStateException(s"Invalid response : $r1")
              if r2 != 30 + i then throw new IllegalStateException(s"Invalid response : $r2")
              (r1, r2)

          ios.toList.sequence
      _ = println(x)
    yield ()
