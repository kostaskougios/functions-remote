package functions.discovery.transportscanners

import functions.Log
import functions.model.{Coordinates, Transport, TransportFunction}

class SeparateClassLoaderTransport(classLoader: ClassLoader) extends TransportScanner:
  override def scan(dependency: String, className: String): TransportFunction =
    Log.info(s"scanning for dependency $dependency")
    (coords, data) =>
      val coordinates = Coordinates(coords)
      Log.info(s"Invoking $coordinates")
      ???

  override def transport = Transport.SeparateClassLoader
