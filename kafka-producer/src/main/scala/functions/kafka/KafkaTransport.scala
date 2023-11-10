package functions.kafka

import functions.model.TransportInput
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.ByteArraySerializer

import java.util.Properties
import scala.util.Using.Releasable

class KafkaTransport(topic: String, producer: KafkaProducer[Array[Byte], Array[Byte]]):
  def transportFunction(trIn: TransportInput): Array[Byte] =
    val coordinates  = trIn.coordinates4
    val pr           = new ProducerRecord(topic, trIn.argsData, trIn.data)
    val coordsHeader = new RecordHeader("coordinates", coordinates.toRawCoordinates.getBytes("UTF-8"))
    pr.headers().add(coordsHeader)
    producer.send(pr)
    Array.emptyByteArray

  def close(): Unit = producer.close()

object KafkaTransport:
  def apply(topic: String, properties: Properties): KafkaTransport =
    new KafkaTransport(topic, new KafkaProducer(properties, new ByteArraySerializer, new ByteArraySerializer))

  given Releasable[KafkaTransport] = resource => resource.close()
