package endtoend.stresstests

import endtoend.tests.{NestedTypeParamsFunctionsImpl, NestedTypeParamsFunctionsReceiverFactory, SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.FiberSocketServer

@main def stressTestServer(): Unit =
  val impl       = new SimpleFunctionsImpl:
    override def add(a: Int, b: Int) =
      //      Thread.sleep(200)
      super.add(a, b)
  val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(impl) ++
    NestedTypeParamsFunctionsReceiverFactory.invokerMap(new NestedTypeParamsFunctionsImpl)

  FiberSocketServer.withServer[Unit](7201, invokerMap): server =>
    while (true)
      val prevCount = server.totalRequestCount
      Thread.sleep(1000)
      val total     = server.totalRequestCount
      val serving   = server.servingCount
      println(s"Total requests: $total , last second: ${total - prevCount} , serving : $serving, activeThreads: ${ThreadCounter
          .countThreads()}, activeSocketConnections: ${server.activeConnectionsCount}")
