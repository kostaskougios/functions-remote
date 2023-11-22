package examples

import endtoend.tests.helidon.{TestHelidonFunctionsImpl, TestsHelidonFunctionsReceiverFactory}
import io.helidon.logging.common.LogConfig
import io.helidon.webserver.WebServer
import io.helidon.webserver.http.{HttpRouting, ServerRequest, ServerResponse}

@main def exampleServer(): Unit =
  LogConfig.configureRuntime()

  val impl       = new TestHelidonFunctionsImpl
  val avroRoutes = TestsHelidonFunctionsReceiverFactory.newAvroTestsHelidonFunctionsHelidonRoutes(impl)
  val jsonRoutes = TestsHelidonFunctionsReceiverFactory.newJsonTestsHelidonFunctionsHelidonRoutes(impl)

  val routeBuilder = HttpRouting.builder()
  avroRoutes.routes(routeBuilder)
  jsonRoutes.routes(routeBuilder)

  val server = WebServer.builder.port(8080).routing(routeBuilder).build.start
  System.out.println("WEB server is up! http://localhost:" + server.port + "/simple-greet")
