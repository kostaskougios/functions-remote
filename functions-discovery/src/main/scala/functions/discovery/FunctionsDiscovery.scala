package functions.discovery

import functions.serializerscanners.GenericScanner
import functions.discovery.transportscanners.{SeparateClassLoaderTransport, TransportScanner}
import functions.model.{CallerFactory, FunctionDetails, FunctionsMethods, RuntimeConfig, Serializer}
import functions.serializerscanners.SerializerScanner
import functions.serializerscanners.reflectivelyLoadObject

import scala.reflect.{ClassTag, classTag}

class FunctionsDiscovery(runtimeConfig: RuntimeConfig, serializerScanners: Seq[SerializerScanner[CallerFactory[_]]], transports: Seq[TransportScanner]):
  def discover[A: ClassTag]: Seq[FunctionDetails[A]] =
    val n       = classTag[A].runtimeClass.getName
    val mo      = loadMethodsObject(n)
    val scanned = serializerScanners.flatMap(scanner => scanner.scan(n).map(factory => (scanner, factory)))
    for
      (scanner, factory) <- scanned
      t                  <- transports
      tr = t.scan(mo.artifactCoordinates, n)
      c  = factory.createCaller(tr).asInstanceOf[A]
    yield FunctionDetails(c, scanner.serializer, t.transport)

  def discoverFirstOne[A: ClassTag]: A = discover.head.function

  private def loadMethodsObject(n: String) =
    reflectivelyLoadObject[FunctionsMethods](runtimeConfig.classLoader, n + "Methods")

object FunctionsDiscovery:
  def apply(runtimeConfig: RuntimeConfig = RuntimeConfig.withDefaults()) =
    val scanners   = Seq(GenericScanner[CallerFactory[_]](runtimeConfig.classLoader, Serializer.Avro, "CallerAvroSerializedFactory"))
    val transports = Seq(new SeparateClassLoaderTransport(runtimeConfig))
    new FunctionsDiscovery(runtimeConfig, scanners, transports)
