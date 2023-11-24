package endtoend.tests.helidon

import io.helidon.webclient.api.WebClient

object HelidonClient:
  def newClient[R](serverPort: Int): WebClient =
    WebClient
      .builder()
      .baseUri(s"http://localhost:$serverPort")
      .keepAlive(true)
      .build()
