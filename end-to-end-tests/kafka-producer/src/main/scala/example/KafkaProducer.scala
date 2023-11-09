package example

import endtoend.tests.kafka.KafkaFunctionsCallerFactory
import endtoend.tests.kafka.model.Person
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.ByteArraySerializer

@main
def kafkaProducer() =
  val producer = new KafkaProducer(KafkaConf.props, new ByteArraySerializer, new ByteArraySerializer)

  val f = KafkaFunctionsCallerFactory.newAvroKafkaFunctions: trIn =>
    val coordinates = trIn.coordinates4
    val pr          = new ProducerRecord("person", trIn.argsData, trIn.data)
    pr.headers()
      .add(new RecordHeader("coordinates", coordinates.toRawCoordinates.getBytes("UTF-8")))
    producer.send(pr)
    Array.emptyByteArray

  try
    f.addPerson("kostas")(2000, Person(5, "KostasK"))
    f.removePerson("kostas-remove")(Person(6, "KostasR"))
    println("OK")
  finally producer.close()
