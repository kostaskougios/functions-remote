package endtoend.tests.cats

import cats.effect.IO
import cats.syntax.all.*
import endtoend.tests.cats.model.Return1
import fs2.io.net.Network
import functions.model.Serializer
import org.http4s.client.UnexpectedStatus
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
          val f = functions(client)
          for r <- f.catsAdd(1, 2) yield r should be(3)
      }
      s"$serializer: catsAddR" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val f = functions(client)
          for r <- f.catsAddR(1, 2) yield r should be(Return1(3))
      }
      s"$serializer: catsAddLR" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val f = functions(client)
          for r <- f.catsAddLR(1, 2) yield r should be(List(Return1(3)))
      }
      s"$serializer: catsDivide" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val f = functions(client)
          for r <- f.catsDivide(4, 2) yield r should be(Left(2))
      }
      s"$serializer: catsDivide error" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val f = functions(client)
          for r <- f.catsDivide(4, 0) yield r should be(Right("Div by zero"))
      }
      s"$serializer: alwaysFails" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val f = functions(client)
          f.alwaysFails(1)
            .map(_ => ???)
            .handleError: t =>
              t shouldBe a[UnexpectedStatus]
      }
      s"$serializer: alwaysFailsBeforeCreatingF" in {
        (server[IO], client[IO]).tupled.use: (_, client) =>
          val f = functions(client)
          f.alwaysFailsBeforeCreatingF(1)
            .map(_ => ???)
            .handleError: t =>
              t shouldBe a[UnexpectedStatus]
      }
  }
