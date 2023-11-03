package functions.proxygenerator.sbt

import com.sksamuel.avro4s.AvroInputStream
import functions.model.GeneratorConfig
import functions.proxygenerator.generateReceiver

class SbtReceiver extends java.util.function.Function[Array[Byte], String] {
  override def apply(in: Array[Byte]): String =
    val is     = AvroInputStream.data[SbtReceiverParams].from(in).build
    val params = is.iterator.next()
    is.close()
    println(s"Generate receiver params = $params")
    generateReceiver(
      GeneratorConfig.withDefaults(),
      params.avroSerialization,
      params.jsonSerialization,
      params.http4sRoutes
    ).generate(params.targetDir, params.exportDependency)
    "OK"
}

case class SbtReceiverParams(
    avroSerialization: Boolean,
    jsonSerialization: Boolean,
    http4sRoutes: Boolean,
    targetDir: String,
    exportDependency: String
)
