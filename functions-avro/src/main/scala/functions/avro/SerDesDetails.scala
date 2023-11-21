package functions.avro

import com.sksamuel.avro4s.*

class SerDesDetails[A](
    val schemaFor: SchemaFor[A],
    val encoder: Encoder[A],
    val decoder: Decoder[A]
):
  val avroOutputStreamBuilder: AvroOutputStreamBuilder[A] = outputStream[A](schemaFor, encoder)
  val avroInputStreamBuilder: AvroInputStreamBuilder[A]   = inputStream[A](using decoder)

  def inputStream[A](using decoder: Decoder[A]): AvroInputStreamBuilder[A] =
    AvroInputStream.binary[A]

  def outputStream[A](schemaFor: SchemaFor[A], encoder: Encoder[A]): AvroOutputStreamBuilder[A] =
    AvroOutputStream.binary[A](using schemaFor, encoder)
