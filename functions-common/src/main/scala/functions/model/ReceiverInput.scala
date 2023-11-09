package functions.model

case class ReceiverInput(
    data: Array[Byte],
    argsData: Array[Byte] = Array.emptyByteArray
)
