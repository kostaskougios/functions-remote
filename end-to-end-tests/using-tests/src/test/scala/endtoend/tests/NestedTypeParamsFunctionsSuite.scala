package endtoend.tests

import endtoend.tests.model.{CombinedReturn, Param1, Param2, Return1}
import org.scalatest.matchers.should.Matchers.*

class NestedTypeParamsFunctionsSuite extends AbstractEndToEndSuite:
  val functions = discovery.discover[NestedTypeParamsFunctions]

  for fd <- functions do
    val function   = fd.function
    val transport  = fd.transport
    val serializer = fd.serializer

    test(s"oneParam using $transport / $serializer") {
      function.oneParam(Param1(2)) should be(Return1(2))
    }

    test(s"twoParams using $transport / $serializer") {
      function.twoParams(Param1(2), Param2(3.2)) should be(Return1(3))
    }

    test(s"seqOfP1 using $transport / $serializer") {
      function.seqOfP1(Seq(Param1(2), Param1(3))) should be(Seq(Return1(2), Return1(3)))
    }

    test(s"seqOfP1P2 using $transport / $serializer") {
      function.seqOfP1P2(Seq(Param1(2), Param1(3)), Seq(Param2(3.5))) should be(
        CombinedReturn(
          Seq(Return1(2), Return1(3)),
          Seq(Return1(3))
        )
      )
    }
