package functions.discovery

import functions.serializerscanners.GenericScanner
import functions.discovery.transportscanners.{SeparateClassLoaderTransport, TransportScanner}
import functions.model.{CallerFactory, FunctionDetails, Serializer}
import functions.serializerscanners.SerializerScanner

import scala.reflect.{ClassTag, classTag}

class FunctionsDiscovery(scanners: Seq[SerializerScanner[CallerFactory[_]]], transports: Seq[TransportScanner]):
  def discover[A: ClassTag]: Seq[FunctionDetails[A]] =
    val n       = classTag[A].runtimeClass.getName
    val scanned = scanners.flatMap(scanner => scanner.scan(n).map(factory => (scanner, factory)))
    for
      (scanner, factory) <- scanned
      t                  <- transports
      tr = t.scan(n)
      c  = factory.createCaller(tr).asInstanceOf[A]
    yield FunctionDetails(c, scanner.serializer, t.transport)

  def discoverFirstOne[A: ClassTag]: A = discover.head.function

object FunctionsDiscovery:
  def apply(classLoader: ClassLoader = Thread.currentThread().getContextClassLoader) =
    val scanners   = Seq(GenericScanner[CallerFactory[_]](classLoader, Serializer.Avro, "CallerAvroSerializedFactory"))
    val transports = Seq(new SeparateClassLoaderTransport())
    new FunctionsDiscovery(scanners, transports)
