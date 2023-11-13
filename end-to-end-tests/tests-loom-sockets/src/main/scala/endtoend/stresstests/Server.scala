package endtoend.stresstests

import endtoend.tests.{SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.FiberSocketServer

@main def stressTestServer(): Unit =
  val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)
  FiberSocketServer.withServer[Unit](7201, invokerMap): server =>
    while (true)
      val prevCount = server.totalRequestCount
      Thread.sleep(1000)
      val total     = server.totalRequestCount
      val serving   = server.servingCount
      println(s"Total requests: $total , last second: ${total - prevCount} , serving : $serving, activeThreads: ${ThreadCounter
          .countThreads()}, activeSocketConnections: ${server.activeConnectionsCount}")
