package example

import endtoend.tests.kafka.{KafkaFunctionsImpl, KafkaFunctionsReceiverFactory}
import functions.kafka.KafkaPoller
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer

import java.time.Duration
import scala.jdk.CollectionConverters.*
import scala.util.Using

@main
def kafkaConsumer() =
  val consumer = new KafkaConsumer(KafkaConf.props, new ByteArrayDeserializer, new ByteArrayDeserializer)
  val m        = KafkaFunctionsReceiverFactory.invokerMap(new KafkaFunctionsImpl)
  Using.resource(KafkaPoller(consumer, m)): poller =>
    consumer.subscribe(Seq("person").asJava)
    consume()
    def consume(): Unit =
      poller.poll(Duration.ofMinutes(10))
      consume()
