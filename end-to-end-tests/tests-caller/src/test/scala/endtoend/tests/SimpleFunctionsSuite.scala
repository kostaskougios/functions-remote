package endtoend.tests

import functions.model.Serializer
import functions.model.Serializer.Avro
import org.scalatest.matchers.should.Matchers.*

/** To run this, first the SimpleFunctions function must be published locally and the .local/dependencies folder should contain the classpath files for
  * SimpleFunctions
  */
class SimpleFunctionsSuite extends AbstractEndToEndSuite:
  val functionBuilder = SimpleFunctionsCallerFactory.newIsolatedClassloaderBuilder(runtimeConfig)

  for (serializer, function) <- Seq(
      (Serializer.Avro, functionBuilder.newAvroSimpleFunctions),
      (Serializer.Json, functionBuilder.newJsonSimpleFunctions)
    )
  do
    test(s"add using $serializer") {
      function.add(5, 6) should be(11)
    }

    test(s"multiply using $serializer") {
      function.multiply(2, 3) should be(6)
    }

    test(s"noArg using $serializer") {
      function.noArg() should be(10)
    }

    test(s"unitResult using $serializer") {
      function.unitResult() should be(())
    }

    test(s"toList using $serializer") {
      function.toList(1, 3) should be(List(1, 2, 3))
    }

    test(s"listParam using $serializer") {
      function.listParam(List(2, 4)) should be(6)
    }

    test(s"failure using $serializer") {
      an[IllegalArgumentException] should be thrownBy:
        function.failure(3)
    }
