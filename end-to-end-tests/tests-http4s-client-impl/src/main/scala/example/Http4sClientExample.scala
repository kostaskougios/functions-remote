package example

import cats.effect.{Concurrent, IO, IOApp}
import cats.syntax.all.*
import fs2.io.net.Network
import io.circe.Decoder
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*

object Http4sClientExample extends IOApp.Simple:
  val run = for {
    joke <- EmberClientBuilder.default[IO].build.use { client =>
      doRequest[IO](client)
    }
    _ = println(joke)
  } yield joke

  final case class Msg(x: Int)
  final case class MsgError(e: Throwable) extends RuntimeException

  given Decoder[Msg]                              = Decoder.derived[Msg]
  given [F[_]: Concurrent]: EntityDecoder[F, Msg] = jsonOf

  def doRequest[F[_]: Concurrent](client: Client[F]): F[Msg] =
    val dsl = new Http4sClientDsl[F] {}

    import dsl.*
    client
      .expect[Msg](POST(uri"http://localhost:8080/Json/endtoend.tests.SimpleFunctions:add"))
      .adaptError { case t => MsgError(t) } // Prevent Client Json Decoding Failure Leaking
