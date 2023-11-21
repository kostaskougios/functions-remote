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
import functions.avro.*

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
  val {{functionN}}SerDes = new SerDesDetails(
    SchemaFor[{{caseClass}}],
    Encoder[{{caseClass}}],
    Decoder[{{caseClass}}]
  )
  val {{functionN}}AvroSchema = {{functionN}}SerDes.schemaFor.schema

  {{^firstParamsRaw.isEmpty}}
  val {{functionN}}ArgsAvroSchemaFor = SchemaFor[{{caseClass}}Args]
  val {{functionN}}ArgsAvroEncoder = Encoder[{{caseClass}}Args]
  val {{functionN}}ArgsAvroDecoder = Decoder[{{caseClass}}Args]
  val {{functionN}}ArgsAvroSchema = {{functionN}}ArgsAvroSchemaFor.schema
  val {{functionN}}ArgsAvroOutputStream = outputStream[{{caseClass}}Args]({{functionN}}ArgsAvroSchemaFor, {{functionN}}ArgsAvroEncoder)
  val {{functionN}}ArgsAvroInputStream = inputStream[{{caseClass}}Args](using {{functionN}}ArgsAvroDecoder)
  {{/firstParamsRaw.isEmpty}}

  {{^isUnitReturnType}}
  val {{functionN}}ReturnTypeAvroSchemaFor = SchemaFor[{{resultNNoFramework}}]
  val {{functionN}}ReturnTypeAvroEncoder = Encoder[{{resultNNoFramework}}]
  val {{functionN}}ReturnTypeAvroDecoder = Decoder[{{resultNNoFramework}}]
  val {{functionN}}ReturnTypeAvroSchema = {{functionN}}ReturnTypeAvroSchemaFor.schema
  val {{functionN}}ReturnTypeAvroOutputStream = outputStream[{{resultNNoFramework}}]({{functionN}}ReturnTypeAvroSchemaFor, {{functionN}}ReturnTypeAvroEncoder)
  val {{functionN}}ReturnTypeAvroInputStream = inputStream[{{resultNNoFramework}}](using {{functionN}}ReturnTypeAvroDecoder)
  {{/isUnitReturnType}}

  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = avroSerialize({{functionN}}SerDes.avroOutputStreamBuilder, value)
  def {{functionN}}Deserializer(data: Array[Byte]): {{caseClass}} = avroDeserialize({{functionN}}AvroSchema, {{functionN}}SerDes.avroInputStreamBuilder, data)
  {{^firstParamsRaw.isEmpty}}
  def {{functionN}}ArgsSerializer(value: {{caseClass}}Args): Array[Byte] = avroSerialize({{functionN}}ArgsAvroOutputStream, value)
  def {{functionN}}ArgsDeserializer(data: Array[Byte]): {{caseClass}}Args = avroDeserialize({{functionN}}ArgsAvroSchema, {{functionN}}ArgsAvroInputStream, data)
  {{/firstParamsRaw.isEmpty}}

  {{^isUnitReturnType}}
  def {{functionN}}ReturnTypeSerializer(value: {{resultNNoFramework}}): Array[Byte] = avroSerialize({{functionN}}ReturnTypeAvroOutputStream, value)
  def {{functionN}}ReturnTypeDeserializer(data: Array[Byte]): {{resultNNoFramework}} = avroDeserialize({{functionN}}ReturnTypeAvroSchema, {{functionN}}ReturnTypeAvroInputStream, data)
  {{/isUnitReturnType}}

  {{/functions}}

