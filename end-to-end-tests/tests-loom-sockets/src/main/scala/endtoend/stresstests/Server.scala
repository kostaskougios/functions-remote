package endtoend.stresstests

import endtoend.tests.{NestedTypeParamsFunctionsImpl, NestedTypeParamsFunctionsReceiverFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.fibers.FiberExecutor
import functions.sockets.FiberSocketServer

import scala.util.Using

@main def stressTestServer(): Unit =
  val impl       = new SimpleFunctionsImpl:
    override def add(a: Int, b: Int) =
      // Thread.sleep(200) // simulate waiting i.e. a jdbc call to see how well the server performs
      super.add(a, b)
  val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(impl) ++
    NestedTypeParamsFunctionsReceiverFactory.invokerMap(new NestedTypeParamsFunctionsImpl)

  Using.resource(FiberExecutor()): executor =>
    Using.resource(FiberSocketServer.startServer(7201, invokerMap, executor, backlog = 256, perStreamQueueSz = 2048)): server =>
      while (true)
        val prevCount = server.stats.totalRequestCount
        Thread.sleep(1000)
        val total     = server.stats.totalRequestCount
        val serving   = server.stats.servingCount
        println(s"Total requests: $total , last second: ${total - prevCount} , serving : $serving, activeThreads: ${ThreadCounter
            .countThreads()}, activeSocketConnections: ${server.stats.activeConnectionsCount}")
