package endtoend.tests

import endtoend.tests.model.{CombinedReturn, Param1, Param2, Return1}
import functions.model.Serializer
import org.scalatest.matchers.should.Matchers.*

class NestedTypeParamsFunctionsSuite extends AbstractEndToEndSuite:
  val functionBuilder = NestedTypeParamsFunctionsCallerFactory.newClassloaderBuilder(runtimeConfig)

  for (serializer, function) <- Seq(
      (Serializer.Avro, functionBuilder.newAvroNestedTypeParamsFunctions),
      (Serializer.Json, functionBuilder.newJsonNestedTypeParamsFunctions)
    )
  do
    test(s"oneParam using $serializer") {
      function.oneParam(Param1(2)) should be(Return1(2))
    }

    test(s"twoParams using $serializer") {
      function.twoParams(Param1(2), Param2(3.2)) should be(Return1(3))
    }

    test(s"seqOfP1 using $serializer") {
      function.seqOfP1(Seq(Param1(2), Param1(3))) should be(Seq(Return1(2), Return1(3)))
    }

    test(s"seqOfP1P2 using $serializer") {
      function.seqOfP1P2(Seq(Param1(2), Param1(3)), Seq(Param2(3.5))) should be(
        CombinedReturn(
          Seq(Return1(2), Return1(3)),
          Seq(Return1(3))
        )
      )
    }
