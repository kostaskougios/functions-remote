package examples

import endtoend.tests.helidon.TestsHelidonFunctionsCallerFactory
import functions.helidon.transport.HelidonTransport
import io.helidon.webclient.api.{ClientResponseTyped, WebClient}

// https://helidon.io/docs/v4/#/se/webclient
@main def exampleClient(): Unit =
  val client = WebClient
    .builder()
    .baseUri("http://localhost:8080")
    .build()

  val transport = new HelidonTransport(client)
  val fAvro     = TestsHelidonFunctionsCallerFactory.newAvroTestsHelidonFunctions(transport.transportFunction)
  val fJson     = TestsHelidonFunctionsCallerFactory.newJsonTestsHelidonFunctions(transport.transportFunction)
  println(fAvro.add(1, 2))
  println(fJson.add(10, 4))

  def simpleGreetRoute(client: WebClient): ClientResponseTyped[Array[Byte]] = client.get.path("/simple-greet/Kos").request(classOf[Array[Byte]])
  def testPostRoute(client: WebClient): ClientResponseTyped[Array[Byte]]    = client.post("/test-post").submit("Kostas".getBytes, classOf[Array[Byte]])

  val getResponse  = simpleGreetRoute(client)
  println(new String(getResponse.entity))
  getResponse.close()
  val postResponse = testPostRoute(client)
  println(new String(postResponse.entity))
  postResponse.close()
