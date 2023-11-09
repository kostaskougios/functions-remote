package example

import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import org.apache.kafka.common.serialization.Deserializer

class PersonDeserializer extends Deserializer[Person]:
  private val b                                              = AvroInputStream.binary[Person]
  private val writerSchema                                   = AvroSchema[Person]
  override def deserialize(topic: String, data: Array[Byte]) =
    b.from(data).build(writerSchema).iterator.next()
