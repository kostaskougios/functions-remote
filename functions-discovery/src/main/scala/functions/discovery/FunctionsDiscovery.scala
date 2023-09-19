package functions.discovery

import functions.discovery.model.FunctionDetails
import functions.discovery.scanners.{AvroScanner, Scanner}

import scala.reflect.{ClassTag, classTag}

class FunctionsDiscovery(scanners: Seq[Scanner], transport: (String, Array[Byte]) => Array[Byte]):
  def discover[A: ClassTag]: Seq[FunctionDetails[A]] =
    val n = classTag[A].runtimeClass.getName
    scanners
      .flatMap(scanner => scanner.scan[A](n).map(factory => (scanner, factory)))
      .map: (s, f) =>
        FunctionDetails(f.createCaller(transport), s.serializer)

  def discoverFirstOne[A: ClassTag]: A = discover.head.function

object FunctionsDiscovery:
  def apply(transport: (String, Array[Byte]) => Array[Byte], classLoader: ClassLoader = Thread.currentThread().getContextClassLoader) =
    val scanners = Seq(AvroScanner(classLoader))
    new FunctionsDiscovery(scanners, transport)
