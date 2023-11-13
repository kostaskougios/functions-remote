package endtoend.tests.loomsockets

import endtoend.tests.{SimpleFunctionsCallerFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.{FiberSocketServer, SocketPool, SocketTransport}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class LoomSocketsSuite extends AnyFunSuite with BeforeAndAfterAll:
  val socketPool   = SocketPool("localhost", 7200, poolSz = 4)
  val transport    = new SocketTransport(socketPool)
  val invokerMap   = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)
  def createServer = FiberSocketServer.withServer[Unit](7200, invokerMap) _

  val caller = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)

  override def afterAll(): Unit = socketPool.close()

  test("client/server") {
    createServer: server =>
      caller.add(5, 6) should be(11)
  }

  test("client/server multiple requests") {
    createServer: server =>
      caller.add(5, 6) should be(11)
      caller.add(1, 2) should be(3)
      caller.add(2, 3) should be(5)
  }

  test("request counter") {
    createServer: server =>
      for i <- 1 to 10 do
        caller.add(5, 6)
        server.totalRequestCount should be(i)
  }
