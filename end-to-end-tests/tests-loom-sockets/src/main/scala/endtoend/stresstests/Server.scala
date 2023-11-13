package endtoend.stresstests

import endtoend.tests.{SimpleFunctionsImpl, SimpleFunctionsReceiverFactory}
import functions.sockets.FiberSocketServer

@main def stressTestServer(): Unit =
  val invokerMap = SimpleFunctionsReceiverFactory.invokerMap(new SimpleFunctionsImpl)
  FiberSocketServer.withServer[Unit](7200, invokerMap): server =>
    while (true)
      val prevCount = server.requestCount
      Thread.sleep(1000)
      val total     = server.requestCount
      println(s"Total requests: $total , last second: ${total - prevCount}")
