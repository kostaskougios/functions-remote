package example

import endtoend.tests.kafka.model.Person
import endtoend.tests.kafka.{KafkaFunctionsCallerFactory, KafkaFunctionsMethods}
import functions.model.Serializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.ByteArraySerializer

/** Not required to create the topic but the commands are:
  *
  * bin/kafka-topics.sh --create --topic add-person --bootstrap-server localhost:9092
  *
  * bin/kafka-topics.sh --describe --topic add-person --bootstrap-server localhost:9092
  */
@main
def kafkaProducer() =
  val producer = new KafkaProducer(KafkaConf.props, new ByteArraySerializer, new ByteArraySerializer)

  val f = KafkaFunctionsCallerFactory.newAvroKafkaFunctions: trIn =>
    val coordinates = KafkaFunctionsMethods.Methods.AddPerson.withSerializer(Serializer.Avro)

    val pr = new ProducerRecord("add-person", trIn.argsData, trIn.data)
    pr.headers()
      .add(new RecordHeader("coordinates", coordinates.toRawCoordinates.getBytes("UTF-8")))
    producer.send(pr)
    Array.emptyByteArray

  try
    f.addPerson("kostas")(2000, Person(5, "KostasK"))
    println("OK")
  finally producer.close()
