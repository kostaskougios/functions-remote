package endtoend.tests.loomsockets

import endtoend.tests.{SimpleFunctionsCallerFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.{FiberSocketServer, SocketPool, SocketTransport}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import concurrent.ExecutionContext.Implicits.global

class LoomSocketsSuite extends AnyFunSuite with BeforeAndAfterAll:
  val socketPool   = SocketPool("localhost", 7200)
  val transport    = new SocketTransport(socketPool)
  val invokerMap   = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)
  def createServer = FiberSocketServer.withServer[Unit](7200, invokerMap) _

  val caller = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)

  override def afterAll(): Unit = socketPool.close()

  test("client/server") {
    createServer: server =>
      caller.add(5, 6) should be(11)
  }

  test("concurrent requests") {
    createServer: server =>
      val all = for i <- 1 to 10000 yield Future:
        caller.add(i, 1) should be(i + 1)
        0 should be(1000)

      for f <- all do Await.ready(f, 4.seconds)
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
        caller.add(i, 1) should be(i + 1)
        server.totalRequestCount should be(i)
  }
