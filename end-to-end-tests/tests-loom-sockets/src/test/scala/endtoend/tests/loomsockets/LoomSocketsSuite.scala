package endtoend.tests.loomsockets

import endtoend.tests.{SimpleFunctions, SimpleFunctionsCallerFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.fibers.FiberExecutor
import functions.sockets.{FiberSocketServer, SocketPool, SocketTransport}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.util.Using

class LoomSocketsSuite extends AnyFunSuite:

  val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)

  def withClientServer(f: (FiberSocketServer, SimpleFunctions) => Unit): Unit =
    Using.resource(FiberExecutor()): executor =>
      Using.resource(FiberSocketServer.startServer(7200, invokerMap, executor)): server =>
        Using.resource(SocketPool("localhost", 7200, executor, poolSz = 4)): pool =>
          val transport = new SocketTransport(pool)
          val caller    = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)
          f(server, caller)

  test("client/server") {
    withClientServer: (_, caller) =>
      caller.add(5, 6) should be(11)
  }

  test("concurrent requests") {
    withClientServer: (_, caller) =>
      val all = for i <- 1 to 10000 yield Future:
        try caller.add(i, 1) should be(i + 1)
        catch case t: Throwable => t.printStackTrace()

      // lets wait all to be ready to make sure the request completed.
      // Then get the result to make sure we don't have an exception
      for f <- all do Await.ready(f, 4.seconds)
      for f <- all do Await.result(f, 1.seconds)
  }

  test("client/server multiple requests") {
    withClientServer: (_, caller) =>
      for i <- 1 to 10000 do caller.add(5 + i, 6) should be(11 + i)
  }

  test("request counter") {
    withClientServer: (server, caller) =>
      for i <- 1 to 10 do
        caller.add(i, 1) should be(i + 1)
        server.totalRequestCount should be(i)
  }
