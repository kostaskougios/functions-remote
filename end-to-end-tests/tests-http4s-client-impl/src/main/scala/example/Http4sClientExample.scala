//package example
//
//import cats.effect.{Concurrent, IO, IOApp}
//import fs2.io.net.Network
//import org.http4s.*
//import org.http4s.Method.*
//import org.http4s.client.Client
//import org.http4s.client.dsl.Http4sClientDsl
//import org.http4s.ember.client.EmberClientBuilder
//import org.http4s.implicits.*
//import fs2.Stream
//import cats.syntax.all.*
//
//object Http4sClientExample extends IOApp.Simple:
//  val run = for {
//    x <- EmberClientBuilder.default[IO].build.use { client =>
//      for
//        r1 <- doJsonRequest[IO](client)
//        r2 <- doAvroRequest[IO](client)
//      yield (r1, r2)
//    }
//    _ = println(x)
//  } yield ()
//
//  def doJsonRequest[F[_]: Concurrent](client: Client[F]): F[Int] =
//    val serializer = new SimpleFunctionsCirceJsonSerializer
//    val dsl        = new Http4sClientDsl[F] {}
//    import dsl.*
//
//    for res <- client.expect[Array[Byte]](
//        PUT(uri"http://localhost:8080/Json/endtoend.tests.SimpleFunctions:add").withBodyStream(
//          Stream.emits(
//            serializer.addSerializer(SimpleFunctionsMethods.Add(2, 3))
//          )
//        )
//      )
//    yield serializer.addReturnTypeDeserializer(res)
//
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
