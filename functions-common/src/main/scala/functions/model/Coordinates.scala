package functions.model

case class Coordinates(method: String, version: String, serializer: Serializer):
  def methodAndVersion: String = s"$method:$version"

object Coordinates:
  def apply(coords: String): Coordinates = coords.split(':') match
    case Array(method, version, serializer) =>
      Coordinates(method, version, Serializer.valueOf(serializer))
    case _                                  => throw new IllegalArgumentException(s"Invalid coordinates : $coords")
