package functions.model

case class Coordinates4(className: String, method: String, version: String, serializer: Serializer, properties: Map[String, Any]):
  def toCoordinatesNoSerializer: String = s"$className:$method:$version"
  def toRawCoordinates: String          = s"$toCoordinatesNoSerializer:$serializer"

case class Coordinates3(className: String, method: String, version: String, properties: Map[String, Any]):
  def withSerializer(serializer: Serializer): Coordinates4 = Coordinates4(className, method, version, serializer, properties)

object Coordinates4:
  def apply(coordinates3: Coordinates3, serializer: Serializer): Coordinates4 =
    Coordinates4(coordinates3.className, coordinates3.method, coordinates3.version, serializer, coordinates3.properties)

  def apply(coords: String): Coordinates4   = coords.split(':') match
    case Array(className, method, version, serializer) =>
      Coordinates4(className, method, version, Serializer.valueOf(serializer), Map.empty)
    case _                                             => throw new IllegalArgumentException(s"Invalid coordinates : $coords")
  def unapply(coords: String): Coordinates4 = apply(coords)
