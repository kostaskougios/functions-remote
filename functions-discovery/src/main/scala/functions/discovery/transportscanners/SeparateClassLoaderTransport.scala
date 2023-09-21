package functions.discovery.transportscanners

import functions.model.TransportFunction
import functions.model.Transport

class SeparateClassLoaderTransport extends TransportScanner:
  override def scan(className: String): TransportFunction = (s, d) => ???

  override def transport = Transport.SeparateClassLoader
