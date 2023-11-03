package functions.proxygenerator.sbt

import com.sksamuel.avro4s.AvroInputStream
import functions.model.GeneratorConfig
import functions.proxygenerator.generateCaller

/** Used by the sbt plugin
  */
class SbtCaller extends java.util.function.Function[Array[Byte], String] {
  override def apply(in: Array[Byte]): String =
    val is     = AvroInputStream.data[SbtCallerParams].from(in).build
    val params = is.iterator.next()
    is.close()
    println(s"Generate caller params = $params")
    generateCaller(
      GeneratorConfig.withDefaults(),
      params.avroSerialization,
      params.jsonSerialization,
      params.classloaderTransport,
      params.http4sClientTransport
    ).generate(params.targetDir, params.exportDependency)
    "OK"
}

case class SbtCallerParams(
    avroSerialization: Boolean,
    jsonSerialization: Boolean,
    classloaderTransport: Boolean,
    http4sClientTransport: Boolean,
    targetDir: String,
    exportDependency: String
)
