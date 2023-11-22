package examples

import endtoend.tests.helidon.TestsHelidonFunctionsCallerFactory
import functions.helidon.transport.HelidonTransport
import io.helidon.webclient.api.WebClient

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
