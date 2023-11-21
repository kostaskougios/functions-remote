package examples

import io.helidon.config.Config
import io.helidon.logging.common.LogConfig
import io.helidon.webserver.WebServer
import io.helidon.webserver.http.{HttpRouting, ServerRequest, ServerResponse}

@main def exampleServer() =
  LogConfig.configureRuntime()
  val config = Config.create
  Config.global(config)

  def routing(routing: HttpRouting.Builder): Unit =
    routing.get("/simple-greet", (req: ServerRequest, res: ServerResponse) => res.send("Hello World!"))

  val server = WebServer.builder.config(config.get("server")).routing(routing).build.start

  System.out.println("WEB server is up! http://localhost:" + server.port + "/simple-greet")
