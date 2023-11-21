package examples

import io.helidon.webclient.api.WebClient

// https://helidon.io/docs/v4/#/se/webclient
@main def exampleClient(): Unit =
  val client = WebClient
    .builder()
    .baseUri("http://localhost:8080")
    .build()

  println(client)
  val response     = client.get.path("/simple-greet").request(classOf[String])
  val entityString = response.entity
  println(entityString)
