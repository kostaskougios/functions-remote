package functions.model

case class Coordinates(className: String, method: String, serializer: Serializer):
  def toCoordinatesNoSerializer: String = s"$className:$method"

object Coordinates:
  def apply(coords: String): Coordinates = coords.split(':') match
    case Array(className, method, serializer) =>
      Coordinates(className, method, Serializer.valueOf(serializer))
    case _                                    => throw new IllegalArgumentException(s"Invalid coordinates : $coords")
