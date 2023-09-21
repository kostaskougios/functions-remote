package functions.model

trait ReceiverFactory[F]:
  def createReceiver(functions: F): FunctionsReceiver
