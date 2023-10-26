package endtoend.tests

import functions.model.Serializer
import functions.model.Serializer.Avro
import org.scalatest.matchers.should.Matchers.*

class SimpleFunctionsSuite extends AbstractEndToEndSuite:
  val serializer = Serializer.Avro
  val transport  = classLoaderTransport.createTransport(s"${BuildInfo.organization}:${BuildInfo.exportedArtifact}:${BuildInfo.version}")
  val function   = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport)

  test(s"add using $transport / $serializer") {
    function.add(5, 6) should be(11)
  }

  test(s"multiply using $transport / $serializer") {
    function.multiply(2, 3) should be(6)
  }

  test(s"noArg using $transport / $serializer") {
    function.noArg() should be(10)
  }

  test(s"toList using $transport / $serializer") {
    function.toList(1, 3) should be(List(1, 2, 3))
  }

  test(s"listParam using $transport / $serializer") {
    function.listParam(List(2, 4)) should be(6)
  }
