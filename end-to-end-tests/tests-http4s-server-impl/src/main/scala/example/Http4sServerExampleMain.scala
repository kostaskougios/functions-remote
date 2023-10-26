package example

import cats.effect.{Async, IO, IOApp}
import com.comcast.ip4s.*
import fs2.io.net.Network
import tests.cats.Server

object Http4sServerExampleMain extends IOApp.Simple:
  val run = QuickstartServer.run[IO]

object QuickstartServer:

  def run[F[_]: Async: Network]: F[Nothing] =
    Server.newServer(port"8080").useForever
