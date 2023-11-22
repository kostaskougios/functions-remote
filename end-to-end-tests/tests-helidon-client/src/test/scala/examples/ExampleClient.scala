package examples

import io.helidon.webclient.api.{ClientResponseTyped, WebClient}

// https://helidon.io/docs/v4/#/se/webclient
@main def exampleClient(): Unit =
  val client = WebClient
    .builder()
    .baseUri("http://localhost:8080")
    .build()

  println(client)

  def simpleGreetRoute(client: WebClient): ClientResponseTyped[Array[Byte]] = client.get.path("/simple-greet/Kos").request(classOf[Array[Byte]])
  def testPostRoute(client: WebClient): ClientResponseTyped[Array[Byte]]    = client.post("/test-post").submit("Kostas".getBytes, classOf[Array[Byte]])

  val getResponse  = simpleGreetRoute(client)
  println(new String(getResponse.entity))
  getResponse.close()
  val postResponse = testPostRoute(client)
  println(new String(postResponse.entity))
  postResponse.close()
