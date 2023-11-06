package functions.model

case class Coordinates4(className: String, method: String, version: String, serializer: Serializer):
  def toCoordinatesNoSerializer: String = s"$className:$method:$version"
  def toRawCoordinates: String          = s"$toCoordinatesNoSerializer:$serializer"

case class Coordinates2(className: String, method: String, version: String):
  def withSerializer(serializer: Serializer): Coordinates4 = Coordinates4(className, method, version, serializer)

object Coordinates4:
  def apply(coordinates2: Coordinates2, serializer: Serializer): Coordinates4 =
    Coordinates4(coordinates2.className, coordinates2.method, coordinates2.version, serializer)

  def apply(coords: String): Coordinates4   = coords.split(':') match
    case Array(className, method, version, serializer) =>
      Coordinates4(className, method, version, Serializer.valueOf(serializer))
    case _                                             => throw new IllegalArgumentException(s"Invalid coordinates : $coords")
  def unapply(coords: String): Coordinates4 = apply(coords)
