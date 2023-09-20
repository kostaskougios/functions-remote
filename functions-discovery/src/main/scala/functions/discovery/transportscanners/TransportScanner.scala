package functions.discovery.transportscanners

import functions.discovery.model.{Transport, TransportFunction}

trait TransportScanner:
  def scan(className: String): TransportFunction
  def transport: Transport
