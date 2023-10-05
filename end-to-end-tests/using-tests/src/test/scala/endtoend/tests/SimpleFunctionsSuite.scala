package endtoend.tests

import functions.discovery.FunctionsDiscovery
import functions.model.RuntimeConfig
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
