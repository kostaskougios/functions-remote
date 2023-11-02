package functions.proxygenerator.sbt

import com.sksamuel.avro4s.AvroInputStream

class SbtCaller extends java.util.function.Function[Array[Byte], String] {
  override def apply(in: Array[Byte]): String =
    val is     = AvroInputStream.data[SbtCallerParams].from(in).build
    val params = is.iterator.next()
    is.close()
    println(s"params = $params")
    "OK"
}

case class SbtCallerParams(
    avroSerialization: Boolean = false,
    jsonSerialization: Boolean = false,
    classloaderTransport: Boolean = false,
    http4sClientTransport: Boolean = false
)
