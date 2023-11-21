package examples

import io.helidon.logging.common.LogConfig
import io.helidon.webserver.WebServer
import io.helidon.webserver.http.{HttpRouting, ServerRequest, ServerResponse}

@main def exampleServer() =
  LogConfig.configureRuntime()

  def routing(routing: HttpRouting.Builder): Unit =
    routing.get("/simple-greet", (req: ServerRequest, res: ServerResponse) => res.send("Hello World!"))

  val server = WebServer.builder.port(8080).routing(routing).build.start
  System.out.println("WEB server is up! http://localhost:" + server.port + "/simple-greet")
