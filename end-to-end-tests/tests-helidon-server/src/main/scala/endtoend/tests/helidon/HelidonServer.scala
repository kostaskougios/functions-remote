package endtoend.tests.helidon

import io.helidon.logging.common.LogConfig
import io.helidon.webserver.WebServer
import io.helidon.webserver.http.HttpRouting

object HelidonServer:
  def withServerDo[R](port: Int, routes: HttpRouting.Builder => Unit*)(f: WebServer => R): R =
    LogConfig.configureRuntime()

    val routeBuilder = HttpRouting.builder()
    for r <- routes do r(routeBuilder)

    val server = WebServer.builder.port(port).routing(routeBuilder).build.start
    try
      f(server)
    finally server.stop()
