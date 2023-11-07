package {{proxypackage}}

import com.sksamuel.avro4s.AvroSchema
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
  private def avroSerialize[A](b: AvroOutputStreamBuilder[A], value: A): Array[Byte] =
    val bos = new ByteArrayOutputStream(4096)
    Using.resource(b.to(bos).build()): os =>
      os.write(value)
    bos.toByteArray

  private def avroDeserialize[A](schema: Schema, b: AvroInputStreamBuilder[A], data: Array[Byte]): A =
    Using.resource(b.from(data).build(schema)): in =>
      in.iterator.next()

  {{#functions}}
  // ----------------------------------------------
  // Serializers for {{functionN}} function
  // ----------------------------------------------
  private val {{functionN}}AvroSchema = AvroSchema[{{caseClass}}]
  private val {{functionN}}AvroOutputStream = AvroOutputStream.binary[{{caseClass}}]
  private val {{functionN}}AvroInputStream = AvroInputStream.binary[{{caseClass}}]

  {{^isUnitReturnType}}
  private val {{functionN}}ReturnTypeAvroSchema = AvroSchema[{{resultNNoFramework}}]
  private val {{functionN}}ReturnTypeAvroOutputStream = AvroOutputStream.binary[{{resultNNoFramework}}]
  private val {{functionN}}ReturnTypeAvroInputStream = AvroInputStream.binary[{{resultNNoFramework}}]
  {{/isUnitReturnType}}

  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = avroSerialize({{functionN}}AvroOutputStream, value)
  def {{functionN}}Deserializer(data: Array[Byte]): {{caseClass}} = avroDeserialize({{functionN}}AvroSchema, {{functionN}}AvroInputStream, data)

  {{^isUnitReturnType}}
  def {{functionN}}ReturnTypeSerializer(value: {{resultNNoFramework}}): Array[Byte] = avroSerialize({{functionN}}ReturnTypeAvroOutputStream, value)
  def {{functionN}}ReturnTypeDeserializer(data: Array[Byte]): {{resultNNoFramework}} = avroDeserialize({{functionN}}ReturnTypeAvroSchema, {{functionN}}ReturnTypeAvroInputStream, data)
  {{/isUnitReturnType}}

  {{/functions}}

