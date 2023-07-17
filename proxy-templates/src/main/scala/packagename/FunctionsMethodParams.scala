package `packagename`

import com.sksamuel.avro4s.AvroOutputStream
import com.sksamuel.avro4s.AvroSchema
import com.sksamuel.avro4s.AvroOutputStreamBuilder
import java.io.ByteArrayOutputStream
import scala.util.Using

trait `FunctionsMethodParams`

object `FunctionsMethodParams`:

  // = foreach caseClasses
  case class `CaseClass`(path: String) extends `FunctionsMethodParams`
  // = end caseClasses
  private def avroSerialize[A](b: AvroOutputStreamBuilder[A], value: A): Array[Byte] =
    val bos = new ByteArrayOutputStream(4096)
    Using.resource(b.to(bos).build()) { os =>
      os.write(value)
    }
    bos.toByteArray

  val avroSerializer: PartialFunction[`FunctionsMethodParams`, Array[Byte]] =
    // = foreach caseClasses
    case value: `CaseClass` =>
      avroSerialize(AvroOutputStream.data[`CaseClass`], value)
    // = end caseClasses
