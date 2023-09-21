package functions.discovery.transportscanners

import functions.model.TransportFunction
import functions.model.Transport

trait TransportScanner:
  def scan(className: String): TransportFunction
  def transport: Transport
