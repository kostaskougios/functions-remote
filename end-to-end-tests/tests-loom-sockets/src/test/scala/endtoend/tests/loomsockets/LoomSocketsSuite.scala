package endtoend.tests.loomsockets

import endtoend.tests.{SimpleFunctionsCallerFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.FiberSocketServer
import org.scalatest.funsuite.AnyFunSuite
import functions.sockets.SocketTransport

class LoomSocketsSuite extends AnyFunSuite:
  val server    = new FiberSocketServer(7200)
  val transport = new SocketTransport("localhost", 7200)

  test("client/server") {
    val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)
    val caller     = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)
    server.acceptOne()
  }
