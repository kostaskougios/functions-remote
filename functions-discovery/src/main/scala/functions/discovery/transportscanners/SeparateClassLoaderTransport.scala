package functions.discovery.transportscanners

import functions.Log
import functions.model.{Coordinates, Transport, TransportFunction}

class SeparateClassLoaderTransport extends TransportScanner:
  override def scan(className: String): TransportFunction =
    Log.info(s"scanning for class $className")
    (coords, data) =>
      val coordinates = Coordinates(coords)
      Log.info(s"Invoking $coordinates")
      ???

  override def transport = Transport.SeparateClassLoader
