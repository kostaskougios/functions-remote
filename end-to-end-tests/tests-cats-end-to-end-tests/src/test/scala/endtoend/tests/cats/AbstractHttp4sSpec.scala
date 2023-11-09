package endtoend.tests.cats

import cats.effect.Async
import cats.effect.testing.scalatest.AsyncIOSpec
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*
import org.scalatest.AsyncTestSuite
import org.scalatest.freespec.AsyncFreeSpec
import tests.cats.Server

class AbstractHttp4sSpec extends AsyncFreeSpec with AsyncTestSuite with AsyncIOSpec:
  val serverUri = uri"http://localhost:7500"

  def server[F[_]: Async: Network] = Server.newServer(port"7500")

  def client[F[_]: Async: Network] =
    EmberClientBuilder
      .default[F]
      .build
