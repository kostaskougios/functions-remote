package functions.discovery.model

trait CallerFactory[A]:
  def createCaller(transport: (String, Array[Byte]) => Array[Byte]): A
