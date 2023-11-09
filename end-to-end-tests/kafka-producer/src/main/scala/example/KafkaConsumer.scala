package example

import endtoend.tests.kafka.KafkaFunctionsAvroSerializer
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.ByteArrayDeserializer

import java.time.Duration
import scala.jdk.CollectionConverters.*

@main
def kafkaConsumer() =
  val consumer = new KafkaConsumer(KafkaConf.props, new ByteArrayDeserializer, new ByteArrayDeserializer)
  try
    consumer.subscribe(Seq("add-person").asJava)
    val r     = consumer.poll(Duration.ofMinutes(10))
    val items = r.iterator().asScala.toList
    println(items.map(toString).mkString("\n"))
  finally consumer.close()

  def toString(cr: ConsumerRecord[Array[Byte], Array[Byte]]) =
    val serializer = KafkaFunctionsAvroSerializer()
    val k          = serializer.addPersonArgsDeserializer(cr.key())
    val v          = serializer.addPersonDeserializer(cr.value())
    s"""
       |topic   = ${cr.topic()}
       |key     = $k
       |value   = $v
       |headers = ${new String(cr.headers().lastHeader("coordinates").value())}
       |""".stripMargin
