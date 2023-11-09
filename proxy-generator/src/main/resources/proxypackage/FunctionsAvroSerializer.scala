package {{proxypackage}}

import com.sksamuel.avro4s.AvroSchema
import com.sksamuel.avro4s.SchemaFor
import com.sksamuel.avro4s.Encoder
import com.sksamuel.avro4s.Decoder
import com.sksamuel.avro4s.AvroOutputStream
import com.sksamuel.avro4s.AvroInputStream
import com.sksamuel.avro4s.AvroOutputStreamBuilder
import com.sksamuel.avro4s.AvroInputStreamBuilder
import java.io.ByteArrayOutputStream
import scala.util.Using
import org.apache.avro.Schema

{{#imports}}
import {{.}}
{{/imports}}

class {{className}}:
  protected def avroSerialize[A](b: AvroOutputStreamBuilder[A], value: A): Array[Byte] =
    val bos = new ByteArrayOutputStream(4096)
    Using.resource(b.to(bos).build()): os =>
      os.write(value)
    bos.toByteArray

  protected def avroDeserialize[A](schema: Schema, b: AvroInputStreamBuilder[A], data: Array[Byte]): A =
    Using.resource(b.from(data).build(schema)): in =>
      in.iterator.next()

  protected def inputStream[A](using decoder: Decoder[A]): AvroInputStreamBuilder[A] =
    AvroInputStream.binary[A]

  protected def outputStream[A](schemaFor: SchemaFor[A], encoder: Encoder[A]): AvroOutputStreamBuilder[A] =
    AvroOutputStream.binary[A](using schemaFor, encoder)
  {{#functions}}
  // ----------------------------------------------
  // Serializers for {{functionN}} function
  // ----------------------------------------------
  val {{functionN}}AvroSchemaFor = SchemaFor[{{caseClass}}]
  val {{functionN}}AvroEncoder = Encoder[{{caseClass}}]
  val {{functionN}}AvroDecoder = Decoder[{{caseClass}}]
  val {{functionN}}AvroSchema = {{functionN}}AvroSchemaFor.schema
  val {{functionN}}AvroOutputStream = outputStream[{{caseClass}}]({{functionN}}AvroSchemaFor, {{functionN}}AvroEncoder)
  val {{functionN}}AvroInputStream = inputStream[{{caseClass}}](using {{functionN}}AvroDecoder)

  {{^isUnitReturnType}}
  val {{functionN}}ReturnTypeAvroSchemaFor = SchemaFor[{{resultNNoFramework}}]
  val {{functionN}}ReturnTypeAvroEncoder = Encoder[{{resultNNoFramework}}]
  val {{functionN}}ReturnTypeAvroDecoder = Decoder[{{resultNNoFramework}}]
  val {{functionN}}ReturnTypeAvroSchema = {{functionN}}ReturnTypeAvroSchemaFor.schema
  val {{functionN}}ReturnTypeAvroOutputStream = outputStream[{{resultNNoFramework}}]({{functionN}}ReturnTypeAvroSchemaFor, {{functionN}}ReturnTypeAvroEncoder)
  val {{functionN}}ReturnTypeAvroInputStream = inputStream[{{resultNNoFramework}}](using {{functionN}}ReturnTypeAvroDecoder)
  {{/isUnitReturnType}}

  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = avroSerialize({{functionN}}AvroOutputStream, value)
  def {{functionN}}Deserializer(data: Array[Byte]): {{caseClass}} = avroDeserialize({{functionN}}AvroSchema, {{functionN}}AvroInputStream, data)

  {{^isUnitReturnType}}
  def {{functionN}}ReturnTypeSerializer(value: {{resultNNoFramework}}): Array[Byte] = avroSerialize({{functionN}}ReturnTypeAvroOutputStream, value)
  def {{functionN}}ReturnTypeDeserializer(data: Array[Byte]): {{resultNNoFramework}} = avroDeserialize({{functionN}}ReturnTypeAvroSchema, {{functionN}}ReturnTypeAvroInputStream, data)
  {{/isUnitReturnType}}

  {{/functions}}

