package endtoend.tests.helidon

import functions.helidon.transport.HelidonTransport
import functions.model.Serializer
import io.helidon.webclient.api.WebClient
import io.helidon.webserver.WebServer
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class EndToEndHelidonSuite extends AnyFunSuite:

  def withServer(serializer: Serializer)(f: TestsHelidonFunctions => Unit): Unit =
    val impl       = new TestHelidonFunctionsImpl
    val avroRoutes = TestsHelidonFunctionsReceiverFactory.newAvroTestsHelidonFunctionsHelidonRoutes(impl)
    val jsonRoutes = TestsHelidonFunctionsReceiverFactory.newJsonTestsHelidonFunctionsHelidonRoutes(impl)
    HelidonServer.withServerDo(0, avroRoutes.routes, jsonRoutes.routes): server =>
      val client    = HelidonClient.newClient(server.port)
      val transport = new HelidonTransport(client)
      val testF     = serializer match
        case Serializer.Avro => TestsHelidonFunctionsCallerFactory.newAvroTestsHelidonFunctions(transport.transportFunction)
        case Serializer.Json => TestsHelidonFunctionsCallerFactory.newJsonTestsHelidonFunctions(transport.transportFunction)

      f(testF)

  test("noArgs") {
    withServer(Serializer.Avro): f =>
      f.add(1, 2) should be(3)
  }
