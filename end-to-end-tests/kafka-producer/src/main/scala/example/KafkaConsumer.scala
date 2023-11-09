package example

import endtoend.tests.kafka.KafkaFunctionsMethods
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import scala.jdk.CollectionConverters.*

@main
def kafkaConsumer() =
  val consumer = new KafkaConsumer(KafkaConf.props, new StringDeserializer, new AddPersonDeserializer)
  try
    consumer.subscribe(Seq("add-person").asJava)
    val r     = consumer.poll(Duration.ofMinutes(10))
    val items = r.iterator().asScala.toList
    println(items.map(toString).mkString("\n"))
  finally consumer.close()

  def toString(cr: ConsumerRecord[String, KafkaFunctionsMethods.AddPerson]) =
    s"""
       |topic   = ${cr.topic()}
       |key     = ${cr.key()}
       |value   = ${cr.value()}
       |headers = ${new String(cr.headers().lastHeader("coordinates").value())}
       |""".stripMargin