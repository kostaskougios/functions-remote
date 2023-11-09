package endtoend.tests.cats

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.AsyncTestSuite
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers.*

class CallerSuite extends AsyncFreeSpec with AsyncTestSuite with AsyncIOSpec:
  "Args" - {
    val avroSerializer = TestsCatsFunctionsAvroSerializer()
    val jsonSerializer = TestsCatsFunctionsCirceJsonSerializer()
    "avro serializer doesn't serialize args" in {
      val f = TestsCatsFunctionsCallerAvroSerializedFactory.createCaller(
        in =>
          in.argsData.isEmpty should be(true)
          IO.pure(avroSerializer.catsAddParamsEmptySecondReturnTypeSerializer(10))
        ,
        false
      )
      f.catsAddParamsEmptySecond(5, 6, "7")().map { i => i should be(10) }
    }

    "json serializer doesn't serialize args" in {
      val f = TestsCatsFunctionsCallerJsonSerializedFactory.createCaller(
        in =>
          in.argsData.isEmpty should be(true)
          IO.pure(jsonSerializer.catsAddParamsEmptySecondReturnTypeSerializer(10))
        ,
        false
      )
      f.catsAddParamsEmptySecond(5, 6, "7")().map { i => i should be(10) }
    }

  }
