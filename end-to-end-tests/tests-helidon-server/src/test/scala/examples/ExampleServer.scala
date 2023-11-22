package examples

import io.helidon.logging.common.LogConfig
import io.helidon.webserver.WebServer
import io.helidon.webserver.http.{HttpRouting, ServerRequest, ServerResponse}

@main def exampleServer(): Unit =
  LogConfig.configureRuntime()

  def testPost(req: ServerRequest, res: ServerResponse): Unit =
    val in = req.content().as(classOf[Array[Byte]])
    res.send(s"Hello ${new String(in)}!")

  def testGet(req: ServerRequest, res: ServerResponse): Unit =
    val name = req.path.pathParameters.get("name")
    res.send(s"Hello $name!")

  def routing(routing: HttpRouting.Builder): Unit =
    routing.get("/simple-greet/{name}", testGet)
    routing.post("/test-post", testPost)

  val server = WebServer.builder.port(8080).routing(routing).build.start
  System.out.println("WEB server is up! http://localhost:" + server.port + "/simple-greet")
