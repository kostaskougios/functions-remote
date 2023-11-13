package endtoend.tests.loomsockets

import endtoend.tests.{SimpleFunctionsCallerFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.FiberSocketServer
import org.scalatest.funsuite.AnyFunSuite
import functions.sockets.SocketTransport

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import org.scalatest.matchers.should.Matchers.*

class LoomSocketsSuite extends AnyFunSuite:
  val server    = new FiberSocketServer(7200)
  val transport = new SocketTransport("localhost", 7200)

  val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)
  val caller     = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)

  test("client/server") {
    Future {
      server.acceptOne(invokerMap)
      Thread.sleep(200)
      server.shutdown()
    }
    Thread.sleep(200)
    caller.add(5, 6) should be(11)
  }

  test("server can be shut down") {
    Future {
      server.acceptOne(invokerMap)
    }
    Thread.sleep(200)
    server.shutdown()
  }
