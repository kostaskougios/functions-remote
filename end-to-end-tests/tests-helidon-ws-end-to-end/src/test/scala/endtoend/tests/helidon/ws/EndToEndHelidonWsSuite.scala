package endtoend.tests.helidon.ws

import org.scalatest.funsuite.AnyFunSuite
import endtoend.tests.helidon.TestsHelidonFunctionsCallerFactory
import functions.fibers.FiberExecutor
import functions.helidon.transport.HelidonWsTransport
import io.helidon.webclient.websocket.WsClient

import java.net.URI

class EndToEndHelidonWsSuite extends AnyFunSuite:
  def withTransport[R](f: HelidonWsTransport => R): R =
    FiberExecutor.withFiberExecutor: executor =>
      val port      = 7210
      val transport = new HelidonWsTransport(executor)

      val uri       = URI.create(s"ws://localhost:$port")
      val webClient = WsClient
        .builder()
        .baseUri(uri)
        .build()
      webClient.connect("/ws-test", transport.clientWsListener)
      try
        f(transport)
      finally transport.close()

  test("calls function"):
    withTransport: transport =>
      val avroF = TestsHelidonFunctionsCallerFactory.newAvroTestsHelidonFunctions(transport.transportFunction)
