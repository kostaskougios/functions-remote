package example

import cats.effect.{Async, Concurrent, IO, IOApp}
import endtoend.tests.cats.TestsCatsFunctionsCallerCirceJsonSerializedFactory
import fs2.Stream
import fs2.io.net.Network
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*

object Http4sClientExample extends IOApp.Simple:
  val run = for {
    x <- EmberClientBuilder.default[IO].build.use { client =>
      for r1 <- doJsonRequest[IO](client)
//        r2 <- doAvroRequest[IO](client)
      yield r1
    }
    _ = println(x)
  } yield ()

  def transportFunction[F[_]: Concurrent](client: Client[F], coordinates: String, data: Array[Byte]): F[Array[Byte]] =
    val dsl = new Http4sClientDsl[F] {}
    import dsl.*

    val Array(clz, method, serializer) = coordinates.split(":")
    val u                              = uri"http://localhost:8080" / clz / method / serializer
    println(s"uri: $u")
    client.expect[Array[Byte]](
      PUT(u).withBodyStream(
        Stream.emits(data)
      )
    )

  def doJsonRequest[F[_]: Async](client: Client[F]): F[Int] =
    val caller = TestsCatsFunctionsCallerCirceJsonSerializedFactory.createCaller[F](transportFunction[F](client, _, _))
    caller.catsAdd(5, 6)

//  def doAvroRequest[F[_]: Concurrent](client: Client[F]): F[Int] =
//    val serializer = new SimpleFunctionsAvroSerializer
//    val dsl        = new Http4sClientDsl[F] {}
//    import dsl.*
//
//    for res <- client.expect[Array[Byte]](
//        PUT(uri"http://localhost:8080/Avro/endtoend.tests.SimpleFunctions:add").withBodyStream(
//          Stream.emits(
//            serializer.addSerializer(SimpleFunctionsMethods.Add(2, 3))
//          )
//        )
//      )
//    yield serializer.addReturnTypeDeserializer(res)
