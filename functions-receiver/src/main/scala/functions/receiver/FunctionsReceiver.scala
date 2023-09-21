package functions.receiver

import functions.receiver.model.RegisteredFunction

class FunctionsReceiver(registeredFunctions: Seq[RegisteredFunction[_]]):
  def invoke(method: String, data: Array[Byte]): Array[Byte] = ???

object FunctionsReceiver:
  def apply(functions: RegisteredFunction[_]*) = new FunctionsReceiver(functions.toList)
