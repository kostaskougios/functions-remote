package functions.kafka

import functions.model.{Coordinates4, ReceiverInput}
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}

import scala.jdk.CollectionConverters.*
import java.time.Duration
import scala.util.Using.Releasable

class KafkaPoller(consumer: KafkaConsumer[Array[Byte], Array[Byte]], invokeMap: Map[Coordinates4, ReceiverInput => Array[Byte]]):
  private val im = invokeMap.map: (c4, i) =>
    (c4.toRawCoordinates, i)

  def poll(duration: Duration): Unit =
    val r = consumer.poll(duration)
    r.iterator.asScala.foreach(execute)

  protected def coordinatesFrom(cr: ConsumerRecord[Array[Byte], Array[Byte]]) = new String(cr.headers().lastHeader("coordinates").value())

  protected def execute(cr: ConsumerRecord[Array[Byte], Array[Byte]]) =
    val coordinatesRaw = coordinatesFrom(cr)
    val f              = im(coordinatesRaw)
    f(ReceiverInput(cr.value(), cr.key()))

  /** Closes the KafkaConsumer
    */
  def close(): Unit = consumer.close()

object KafkaPoller:
  given Releasable[KafkaPoller] = resource => resource.close()
