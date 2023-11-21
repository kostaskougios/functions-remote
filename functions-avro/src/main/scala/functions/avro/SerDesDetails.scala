package functions.avro

import com.sksamuel.avro4s.*

class SerDesDetails[A](
    val schemaFor: SchemaFor[A],
    val encoder: Encoder[A],
    val decoder: Decoder[A]
):
  val avroOutputStreamBuilder: AvroOutputStreamBuilder[A] = AvroOutputStream.binary[A](using schemaFor, encoder)
  val avroInputStreamBuilder: AvroInputStreamBuilder[A]   = AvroInputStream.binary[A](using decoder)
