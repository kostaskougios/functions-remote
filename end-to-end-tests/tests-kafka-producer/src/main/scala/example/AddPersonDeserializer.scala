package example

import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import endtoend.tests.kafka.{KafkaFunctionsAvroSerializer, KafkaFunctionsMethods}
import endtoend.tests.kafka.model.Person
import org.apache.kafka.common.serialization.Deserializer

class AddPersonDeserializer extends Deserializer[KafkaFunctionsMethods.AddPerson]:
  private val b                                              = AvroInputStream.binary[KafkaFunctionsMethods.AddPerson]
  private val s                                              = new KafkaFunctionsAvroSerializer
  override def deserialize(topic: String, data: Array[Byte]) =
    s.addPersonDeserializer(data)
