package endtoend.tests

import functions.model.Serializer
import functions.model.Serializer.Avro
import org.scalatest.matchers.should.Matchers.*

class SimpleFunctionsSuite extends AbstractEndToEndSuite:
  val functionBuilder = SimpleFunctionsCallerFactory.newClassloaderBuilder(runtimeConfig)

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

    test(s"toList using $serializer") {
      function.toList(1, 3) should be(List(1, 2, 3))
    }

    test(s"listParam using $serializer") {
      function.listParam(List(2, 4)) should be(6)
    }
