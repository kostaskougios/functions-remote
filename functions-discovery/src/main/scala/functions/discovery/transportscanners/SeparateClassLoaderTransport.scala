package functions.discovery.transportscanners

import functions.Log
import functions.model.TransportFunction
import functions.model.Transport

class SeparateClassLoaderTransport extends TransportScanner:
  override def scan(className: String): TransportFunction = (coordinates, data) =>
    Log.info(s"Invoking $coordinates")
    ???

  override def transport = Transport.SeparateClassLoader
