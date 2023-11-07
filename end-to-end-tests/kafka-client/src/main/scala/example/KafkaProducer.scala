package example

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.StringSerializer

/** bin/kafka-topics.sh --create --topic people --bootstrap-server localhost:9092
  *
  * bin/kafka-topics.sh --describe --topic people --bootstrap-server localhost:9092
  */
@main
def kafkaProducer() =
  val producer = new KafkaProducer[String, Person](KafkaConf.props, new StringSerializer, new PersonSerializer)
  try
    val pr = new ProducerRecord("people", "kostas", Person(1, "Kostas"))
    pr.headers().add(new RecordHeader("coordinates", "LsFunctions.ls/1.0".getBytes("UTF-8")))
    producer.send(pr)
    println("OK")
  finally producer.close()
