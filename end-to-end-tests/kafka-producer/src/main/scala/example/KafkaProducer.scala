package example

import endtoend.tests.kafka.KafkaFunctionsMethods
import endtoend.tests.kafka.model.Person
import functions.model.Serializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.StringSerializer

/** bin/kafka-topics.sh --create --topic people --bootstrap-server localhost:9092
  *
  * bin/kafka-topics.sh --describe --topic people --bootstrap-server localhost:9092
  */
@main
def kafkaProducer() =
  val producer = new KafkaProducer[String, KafkaFunctionsMethods.AddPerson](KafkaConf.props, new StringSerializer, new AddPersonSerializer)
  try
    val pr          = new ProducerRecord("add-person", "kostas", KafkaFunctionsMethods.AddPerson(1000, Person(1, "Kostas")))
    val coordinates = KafkaFunctionsMethods.Methods.AddPerson.withSerializer(Serializer.Avro)
    pr.headers()
      .add(new RecordHeader("coordinates", coordinates.toRawCoordinates.getBytes("UTF-8")))
    producer.send(pr)
    println("OK")
  finally producer.close()
