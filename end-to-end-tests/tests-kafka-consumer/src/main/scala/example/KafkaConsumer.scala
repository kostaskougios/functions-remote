package example

import endtoend.tests.kafka.{KafkaFunctionsAvroSerializer, KafkaFunctionsImpl, KafkaFunctionsReceiverFactory}
import functions.model.{Coordinates4, ReceiverInput}
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.ByteArrayDeserializer

import java.time.Duration
import scala.jdk.CollectionConverters.*

@main
def kafkaConsumer() =
  val consumer = new KafkaConsumer(KafkaConf.props, new ByteArrayDeserializer, new ByteArrayDeserializer)
  val m        = KafkaFunctionsReceiverFactory.invokerMap(new KafkaFunctionsImpl)
  try
    consumer.subscribe(Seq("person").asJava)
    consume()
  finally consumer.close()

  def consume(): Unit =
    val r     = consumer.poll(Duration.ofMinutes(10))
    println("Received msg")
    val items = r.iterator().asScala.toList
    items.foreach(execute)
    consume()

  def execute(cr: ConsumerRecord[Array[Byte], Array[Byte]]) =
    val coordinatesRaw = new String(cr.headers().lastHeader("coordinates").value())
    val coordinates    = Coordinates4(coordinatesRaw)
    val f              = m(coordinates)
    f(ReceiverInput(cr.value(), cr.key()))
