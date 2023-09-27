package functions.model

case class Coordinates(group: String, artifact: String, version: String, className: String, method: String, serializer: Serializer):
  def toCoordinatesNoSerializer: String = s"$group:$artifact:$version:$className:$method"

object Coordinates:
  def apply(coords: String): Coordinates = coords.split(':') match
    case Array(group, artifact, version, className, method, serializer) =>
      Coordinates(group, artifact, version, className, method, Serializer.valueOf(serializer))
    case _                                                              => throw new IllegalArgumentException(s"Invalid coordinates : $coords")
