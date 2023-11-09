package example

import endtoend.tests.kafka.KafkaFunctionsAvroSerializer
import functions.model.Coordinates4
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.ByteArrayDeserializer

import java.time.Duration
import scala.jdk.CollectionConverters.*

@main
def kafkaConsumer() =
  val consumer = new KafkaConsumer(KafkaConf.props, new ByteArrayDeserializer, new ByteArrayDeserializer)
  try
    consumer.subscribe(Seq("person").asJava)
    consume()
  finally consumer.close()

  def consume(): Unit =
    val r     = consumer.poll(Duration.ofMinutes(10))
    val items = r.iterator().asScala.toList
    println(items.map(toString).mkString("\n"))
    consume()

  def toString(cr: ConsumerRecord[Array[Byte], Array[Byte]]) =
    val serializer     = KafkaFunctionsAvroSerializer()
    val coordinatesRaw = new String(cr.headers().lastHeader("coordinates").value())
    val coordinates    = Coordinates4(coordinatesRaw)

    val k = serializer.addPersonArgsDeserializer(cr.key())
    val v = coordinates.method match
      case "addPerson"    => serializer.addPersonDeserializer(cr.value())
      case "removePerson" => serializer.removePersonDeserializer(cr.value())
    s"""
       |topic   = ${cr.topic()}
       |key     = $k
       |value   = $v
       |headers = $coordinatesRaw
       |""".stripMargin
