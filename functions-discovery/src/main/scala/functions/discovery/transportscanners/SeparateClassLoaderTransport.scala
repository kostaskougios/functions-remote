package functions.discovery.transportscanners

import functions.discovery.model.{Transport, TransportFunction}

class SeparateClassLoaderTransport extends TransportScanner:
  override def scan(className: String): TransportFunction = (s, d) => ???

  override def transport = Transport.SeparateClassLoader
