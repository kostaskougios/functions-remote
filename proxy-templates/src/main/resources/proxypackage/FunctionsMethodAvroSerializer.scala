package {{proxypackage}}

import com.sksamuel.avro4s.AvroOutputStream
import com.sksamuel.avro4s.AvroOutputStreamBuilder
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

  val avroParamsSerializer: PartialFunction[{{methodParams}}, Array[Byte]] =
    {{#functions}}
    case value: {{caseClass}} =>
      avroSerialize(AvroOutputStream.data[{{caseClass}}], value)
    {{/functions}}

  {{#functions}}
  // Serializers for {{functionN}}({{params}})
  private val {{functionN}}AvroOutputStream = AvroOutputStream.data[{{caseClass}}]
  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = avroSerialize({{functionN}}AvroOutputStream, value)
  {{/functions}}

