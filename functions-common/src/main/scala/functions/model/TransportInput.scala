package functions.model

trait TransportInput:
  def coordinates4: Coordinates4
  def data: Array[Byte]

case class StdTransportInput(coordinates4: Coordinates4, data: Array[Byte]) extends TransportInput
