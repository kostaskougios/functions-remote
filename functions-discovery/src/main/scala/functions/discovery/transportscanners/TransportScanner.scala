package functions.discovery.transportscanners

import functions.model.{Coordinates, Transport, TransportFunction}

trait TransportScanner:
  def scan(dependency: String,className:String): TransportFunction
  def transport: Transport
