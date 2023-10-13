package endtoend.tests

import org.scalatest.matchers.should.Matchers.*

class SimpleFunctionsSuite extends AbstractEndToEndSuite:
  val functions = discovery.discover[SimpleFunctions]

  for fd <- functions do
    val function   = fd.function
    val transport  = fd.transport
    val serializer = fd.serializer

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
