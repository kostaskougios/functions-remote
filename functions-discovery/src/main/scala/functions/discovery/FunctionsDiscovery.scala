package functions.discovery

import functions.discovery.model.FunctionDetails
import functions.discovery.serializerscanners.{AvroSerializerScanner, SerializerScanner}
import functions.discovery.transportscanners.{SeparateClassLoaderTransport, TransportScanner}

import scala.reflect.{ClassTag, classTag}

class FunctionsDiscovery(scanners: Seq[SerializerScanner], transports: Seq[TransportScanner]):
  def discover[A: ClassTag]: Seq[FunctionDetails[A]] =
    val n       = classTag[A].runtimeClass.getName
    val scanned = scanners.flatMap(scanner => scanner.scan[A](n).map(factory => (scanner, factory)))
    for
      (scanner, factory) <- scanned
      t                  <- transports
      tr = t.scan(n)
      c  = factory.createCaller(tr)
    yield FunctionDetails(c, scanner.serializer, t.transport)

  def discoverFirstOne[A: ClassTag]: A = discover.head.function

object FunctionsDiscovery:
  def apply(classLoader: ClassLoader = Thread.currentThread().getContextClassLoader) =
    val scanners   = Seq(AvroSerializerScanner(classLoader))
    val transports = Seq(new SeparateClassLoaderTransport())
    new FunctionsDiscovery(scanners, transports)
