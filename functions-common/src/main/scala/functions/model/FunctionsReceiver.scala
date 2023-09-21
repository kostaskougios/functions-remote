package functions.model

trait FunctionsReceiver:
  def invoke: PartialFunction[(String, Array[Byte]), Array[Byte]]
