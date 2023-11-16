package endtoend.stresstests

import endtoend.tests.{NestedTypeParamsFunctionsImpl, NestedTypeParamsFunctionsReceiverFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.fibers.FiberExecutor
import functions.sockets.FiberSocketServer

import scala.util.Using

@main def stressTestServer(): Unit =
  val impl       = new SimpleFunctionsImpl:
    override def add(a: Int, b: Int) =
      Thread.sleep(200)
      super.add(a, b)
  val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(impl) ++
    NestedTypeParamsFunctionsReceiverFactory.invokerMap(new NestedTypeParamsFunctionsImpl)

  Using.resource(FiberExecutor()): executor =>
    Using.resource(FiberSocketServer.startServer(7201, invokerMap, executor)): server =>
      while (true)
        val prevCount = server.totalRequestCount
        Thread.sleep(1000)
        val total     = server.totalRequestCount
        val serving   = server.servingCount
        println(s"Total requests: $total , last second: ${total - prevCount} , serving : $serving, activeThreads: ${ThreadCounter
            .countThreads()}, activeSocketConnections: ${server.activeConnectionsCount}")
