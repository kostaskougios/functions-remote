package examples

import io.helidon.webclient.api.WebClient

// https://helidon.io/docs/v4/#/se/webclient
@main def exampleClient(): Unit =
  val client = WebClient
    .builder()
    .baseUri("http://localhost:8080")
    .build()

  println(client)
  val getResponse  = client.get.path("/simple-greet").request(classOf[Array[Byte]])
  println(new String(getResponse.entity))
  val postResponse = client.post("/test-post").submit("Kostas".getBytes, classOf[Array[Byte]])
  println(new String(postResponse.entity))
