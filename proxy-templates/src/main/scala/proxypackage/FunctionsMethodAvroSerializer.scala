package `proxypackage`

import com.sksamuel.avro4s.AvroOutputStream
import com.sksamuel.avro4s.AvroOutputStreamBuilder
import java.io.ByteArrayOutputStream
import scala.util.Using

/*=imports*/

object `functionsMethodAvroSerializer`:
  private def avroSerialize[A](b: AvroOutputStreamBuilder[A], value: A): Array[Byte] =
    val bos = new ByteArrayOutputStream(4096)
    Using.resource(b.to(bos).build()): os =>
      os.write(value)

    bos.toByteArray

  val avroSerializer: PartialFunction[`methodParams`, Array[Byte]] =
    // foreach caseClasses
    case value: `caseClass` =>
      avroSerialize(AvroOutputStream.data[`caseClass`], value)
    // end caseClasses
