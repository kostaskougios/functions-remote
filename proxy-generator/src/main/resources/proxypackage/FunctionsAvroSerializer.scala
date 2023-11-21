package {{proxypackage}}

import com.sksamuel.avro4s.SchemaFor
import com.sksamuel.avro4s.Encoder
import com.sksamuel.avro4s.Decoder
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
  val {{functionN}}ArgsSerDes = new SerDesDetails(
    SchemaFor[{{caseClass}}Args],
    Encoder[{{caseClass}}Args],
    Decoder[{{caseClass}}Args]
  )
  val {{functionN}}ArgsAvroSchema = {{functionN}}ArgsSerDes.schemaFor.schema
  {{/firstParamsRaw.isEmpty}}

  {{^isUnitReturnType}}
  val {{functionN}}ReturnTypeSerDes = new SerDesDetails(
    SchemaFor[{{resultNNoFramework}}],
    Encoder[{{resultNNoFramework}}],
    Decoder[{{resultNNoFramework}}]
  )
  val {{functionN}}ReturnTypeAvroSchema = {{functionN}}ReturnTypeSerDes.schemaFor.schema
  {{/isUnitReturnType}}

  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = avroSerialize({{functionN}}SerDes.avroOutputStreamBuilder, value)
  def {{functionN}}Deserializer(data: Array[Byte]): {{caseClass}} = avroDeserialize({{functionN}}AvroSchema, {{functionN}}SerDes.avroInputStreamBuilder, data)
  {{^firstParamsRaw.isEmpty}}
  def {{functionN}}ArgsSerializer(value: {{caseClass}}Args): Array[Byte] = avroSerialize({{functionN}}ArgsSerDes.avroOutputStreamBuilder, value)
  def {{functionN}}ArgsDeserializer(data: Array[Byte]): {{caseClass}}Args = avroDeserialize({{functionN}}ArgsAvroSchema, {{functionN}}ArgsSerDes.avroInputStreamBuilder, data)
  {{/firstParamsRaw.isEmpty}}

  {{^isUnitReturnType}}
  def {{functionN}}ReturnTypeSerializer(value: {{resultNNoFramework}}): Array[Byte] = avroSerialize({{functionN}}ReturnTypeSerDes.avroOutputStreamBuilder, value)
  def {{functionN}}ReturnTypeDeserializer(data: Array[Byte]): {{resultNNoFramework}} = avroDeserialize({{functionN}}ReturnTypeAvroSchema, {{functionN}}ReturnTypeSerDes.avroInputStreamBuilder, data)
  {{/isUnitReturnType}}

  {{/functions}}

