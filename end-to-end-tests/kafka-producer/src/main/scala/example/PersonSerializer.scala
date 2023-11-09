package example

import com.sksamuel.avro4s.{AvroOutputStream, AvroOutputStreamBuilder}
import org.apache.kafka.common.serialization.Serializer

import java.io.ByteArrayOutputStream

class PersonSerializer extends Serializer[Person]:
  private val b                                       = AvroOutputStream.binary[Person]
  override def serialize(topic: String, data: Person) =
    val o  = new ByteArrayOutputStream
    val ao = b.to(o).build()
    ao.write(data)
    ao.close()
    o.close()
    o.toByteArray
