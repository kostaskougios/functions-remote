package endtoend.tests.cats

import cats.effect.IO
import cats.syntax.all.*
import endtoend.tests.cats.model.Return1
import fs2.io.net.Network
import functions.model.Serializer
import org.scalatest.matchers.should.Matchers.*

class EndToEndHttp4sRoutesSpec extends AbstractHttp4sSpec:

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
      s"$serializer: catsAddR" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val avroCaller = functions(client)
          for r <- avroCaller.catsAddR(1, 2) yield r should be(Return1(3))
      }
      s"$serializer: catsAddLR" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val avroCaller = functions(client)
          for r <- avroCaller.catsAddLR(1, 2) yield r should be(List(Return1(3)))
      }
  }
