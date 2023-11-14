package endtoend.tests.loomsockets

import endtoend.tests.{SimpleFunctions, SimpleFunctionsCallerFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.{FiberSocketServer, SocketPool, SocketTransport}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

class LoomSocketsSuite extends AnyFunSuite:

  def withCallerDo(f: SimpleFunctions => Unit): Unit =
    val socketPool = SocketPool("localhost", 7200)
    try
      val transport = new SocketTransport(socketPool)
      val caller    = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)
      f(caller)
    finally socketPool.close()

  val invokerMap   = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)
  def createServer = FiberSocketServer.withServer[Unit](7200, invokerMap) _

  test("client/server") {
    createServer: server =>
      withCallerDo: caller =>
        caller.add(5, 6) should be(11)
  }

  test("concurrent requests") {
    createServer: server =>
      withCallerDo: caller =>
        val all = for i <- 1 to 10000 yield Future:
          caller.add(i, 1) should be(i + 1)

        // lets wait all to be ready to make sure the request completed.
        // Then get the result to make sure we don't have an exception
        for f <- all do Await.ready(f, 8.seconds)
        for f <- all do Await.result(f, 1.seconds)
  }

  test("client/server multiple requests") {
    createServer: server =>
      withCallerDo: caller =>
        caller.add(5, 6) should be(11)
        caller.add(1, 2) should be(3)
        caller.add(2, 3) should be(5)
  }

  test("request counter") {
    createServer: server =>
      withCallerDo: caller =>
        for i <- 1 to 10 do
          caller.add(i, 1) should be(i + 1)
          server.totalRequestCount should be(i)
  }
