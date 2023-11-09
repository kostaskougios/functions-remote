package endtoend.tests.kafka

import endtoend.tests.kafka.KafkaFunctionsMethods.AddPersonArgs
import endtoend.tests.kafka.model.Person
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class KafkaCallerSuite extends AnyFunSuite:
  val avroSerializer = KafkaFunctionsAvroSerializer()
  val jsonSerializer = KafkaFunctionsCirceJsonSerializer()
  test("args avro serialized") {
    val f = KafkaFunctionsCallerFactory.newAvroKafkaFunctions: trIn =>
      avroSerializer.addPersonArgsDeserializer(trIn.argsData) should be(AddPersonArgs("the-id"))
      Array.emptyByteArray

    f.addPerson("the-id")(5, Person(15, "kostas"))
  }

  test("args json serialized") {
    val f = KafkaFunctionsCallerFactory.newJsonKafkaFunctions: trIn =>
      jsonSerializer.addPersonArgsDeserializer(trIn.argsData) should be(AddPersonArgs("the-id"))
      Array.emptyByteArray

    f.addPerson("the-id")(5, Person(15, "kostas"))
  }
