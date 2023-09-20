package functions.discovery.model

type TransportFunction = (String, Array[Byte]) => Array[Byte]
trait CallerFactory[A]:
  def createCaller(transport: TransportFunction): A
