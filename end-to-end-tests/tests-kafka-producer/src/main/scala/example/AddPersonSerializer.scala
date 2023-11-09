package example

import endtoend.tests.kafka.{KafkaFunctionsAvroSerializer, KafkaFunctionsMethods}
import org.apache.kafka.common.serialization.Serializer

class AddPersonSerializer extends Serializer[KafkaFunctionsMethods.AddPerson]:
  private val s                                                                = new KafkaFunctionsAvroSerializer
  override def serialize(topic: String, data: KafkaFunctionsMethods.AddPerson) =
    s.addPersonSerializer(data)
