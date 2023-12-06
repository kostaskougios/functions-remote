package endtoend.tests.helidon.ws

import endtoend.tests.helidon.impl.CountingHelidonFunctionsImpl
import endtoend.tests.helidon.model.Return1
import endtoend.tests.helidon.{TestsHelidonFunctions, TestsHelidonFunctionsCallerFactory, TestsHelidonFunctionsReceiverFactory}
import functions.fibers.FiberExecutor
import functions.helidon.transport.HelidonWsTransport
import functions.helidon.transport.exceptions.RemoteFunctionFailedException
import functions.helidon.ws.ServerWsListener
import functions.model.Serializer
import io.helidon.webclient.websocket.WsClient
import io.helidon.webserver.WebServer
import io.helidon.webserver.websocket.WsRouting
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import java.net.URI

class EndToEndHelidonWsSuite extends AnyFunSuite:
  def withServer[R](f: WebServer => R): R =
    val impl      = new CountingHelidonFunctionsImpl
    val invokeMap = TestsHelidonFunctionsReceiverFactory.invokerMap(impl)
    val listener  = new ServerWsListener(invokeMap)

    val wsB    = WsRouting.builder().endpoint("/ws-test", listener)
    val server = WebServer.builder
      .port(0)
      .addRouting(wsB)
      .build
      .start
    try f(server)
    finally server.stop()

  def withTransport[R](serverPort: Int, serializer: Serializer)(f: TestsHelidonFunctions => R): R =
    FiberExecutor.withFiberExecutor: executor =>
      val transport = new HelidonWsTransport(executor)
      val uri       = URI.create(s"ws://localhost:$serverPort")
      val webClient = WsClient
        .builder()
        .baseUri(uri)
        .build()
      webClient.connect("/ws-test", transport.clientWsListener)
      val fun       = serializer match
        case Serializer.Avro => TestsHelidonFunctionsCallerFactory.newAvroTestsHelidonFunctions(transport.transportFunction)
        case Serializer.Json => TestsHelidonFunctionsCallerFactory.newJsonTestsHelidonFunctions(transport.transportFunction)
      try
        f(fun)
      finally transport.close()

  def runTest(serializer: Serializer)(f: TestsHelidonFunctions => Unit): Unit =
    withServer: server =>
      withTransport(server.port, serializer): transport =>
        f(transport)

  for serializer <- Seq(Serializer.Avro, Serializer.Json) do
    test(s"$serializer: add"):
      runTest(serializer): f =>
        f.add(1, 3) should be(4)

    test(s"$serializer: addLR"):
      runTest(serializer): f =>
        f.addLR(2, 3) should be(List(Return1(5)))

    test(s"$serializer: noArgs"):
      runTest(serializer): f =>
        f.noArgs() should be(5)

    test(s"$serializer: alwaysFails"):
      runTest(serializer): f =>
        an[RemoteFunctionFailedException] should be thrownBy:
          f.alwaysFails(6)

    test(s"$serializer: addR"):
      runTest(serializer): f =>
        f.addR(5, 7) should be(Return1(12))

    test(s"$serializer: divide left"):
      runTest(serializer): f =>
        f.divide(10, 2) should be(Left(5))

    test(s"$serializer: divide right"):
      runTest(serializer): f =>
        f.divide(10, 0) should be(Right("/ by zero"))

    test(s"$serializer: noArgsUnitReturnType"):
      runTest(serializer): f =>
        f.noArgsUnitReturnType() should be(())

//    test(s"$serializer: calling multiple functions sequentially"):
//      runTest(serializer): f =>
//        for i <- 1 to 10000 do
//          f.add(i, 1) should be(i + 1)
//          f.addLR(i, 1) should be(List(Return1(i + 1)))
//
//    test(s"$serializer: calling multiple functions concurrently"):
//      runTest(serializer): f =>
//        FiberExecutor.withFiberExecutor: executor =>
//          val fibers = for i <- 1 to 10000 yield executor.submit:
//            f.add(i, 1) should be(i + 1)
//            f.addLR(i, 1) should be(List(Return1(i + 1)))
//
//          for f <- fibers do f.get()
