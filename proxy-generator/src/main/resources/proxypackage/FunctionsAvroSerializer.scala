package {{proxypackage}}

import com.sksamuel.avro4s.AvroOutputStream
import com.sksamuel.avro4s.AvroInputStream
import com.sksamuel.avro4s.AvroOutputStreamBuilder
import com.sksamuel.avro4s.AvroInputStreamBuilder
import java.io.ByteArrayOutputStream
import scala.util.Using

{{#imports}}
import {{.}}
{{/imports}}

class {{className}}:
  private def avroSerialize[A](b: AvroOutputStreamBuilder[A], value: A): Array[Byte] =
    val bos = new ByteArrayOutputStream(4096)
    Using.resource(b.to(bos).build()): os =>
      os.write(value)
    bos.toByteArray

  private def avroDeserialize[A](b: AvroInputStreamBuilder[A], data: Array[Byte]): A =
    Using.resource(b.from(data).build): in =>
      in.iterator.next()

  {{#functions}}
  // ----------------------------------------------
  // Serializers for {{functionN}} function
  // ----------------------------------------------
  private val {{functionN}}AvroOutputStream = AvroOutputStream.data[{{caseClass}}]
  private val {{functionN}}AvroInputStream = AvroInputStream.data[{{caseClass}}]

  private val {{functionN}}ReturnTypeAvroOutputStream = AvroOutputStream.data[{{resultNNoFramework}}]
  private val {{functionN}}ReturnTypeAvroInputStream = AvroInputStream.data[{{resultNNoFramework}}]

  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = avroSerialize({{functionN}}AvroOutputStream, value)
  def {{functionN}}Deserializer(data: Array[Byte]): {{caseClass}} = avroDeserialize({{functionN}}AvroInputStream, data)

  def {{functionN}}ReturnTypeSerializer(value: {{resultNNoFramework}}): Array[Byte] = avroSerialize({{functionN}}ReturnTypeAvroOutputStream, value)
  def {{functionN}}ReturnTypeDeserializer(data: Array[Byte]): {{resultNNoFramework}} = avroDeserialize({{functionN}}ReturnTypeAvroInputStream, data)

  {{/functions}}

