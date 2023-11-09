package functions.model

trait TransportInput:
  def coordinates4: Coordinates4
  def data: Array[Byte]
  def args: Array[Any]
  def argsData: Array[Byte]

case class StdTransportInput(
    coordinates4: Coordinates4,
    data: Array[Byte],
    args: Array[Any] = Array.empty,
    argsData: Array[Byte] = Array.emptyByteArray
) extends TransportInput
