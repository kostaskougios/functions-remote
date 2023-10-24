package functions.model

case class Coordinates3(className: String, method: String, serializer: Serializer):
  def toCoordinatesNoSerializer: String = s"$className:$method"

case class Coordinates2(className: String, method: String)

object Coordinates3:
  def apply(coords: String): Coordinates3   = coords.split(':') match
    case Array(className, method, serializer) =>
      Coordinates3(className, method, Serializer.valueOf(serializer))
    case _                                    => throw new IllegalArgumentException(s"Invalid coordinates : $coords")
  def unapply(coords: String): Coordinates3 = apply(coords)