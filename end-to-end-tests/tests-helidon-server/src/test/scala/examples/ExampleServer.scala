package examples

import endtoend.tests.helidon.{HelidonServer, TestHelidonFunctionsImpl, TestsHelidonFunctionsReceiverFactory}

@main def exampleServer(): Unit =
  val impl       = new TestHelidonFunctionsImpl
  val avroRoutes = TestsHelidonFunctionsReceiverFactory.newAvroTestsHelidonFunctionsHelidonRoutes(impl)
  val jsonRoutes = TestsHelidonFunctionsReceiverFactory.newJsonTestsHelidonFunctionsHelidonRoutes(impl)

  HelidonServer.withServerDo(8080, avroRoutes.routes, jsonRoutes.routes): server =>
    System.out.println("WEB server is up! http://localhost:" + server.port + "/simple-greet")
    Thread.sleep(1000 * 84600)
