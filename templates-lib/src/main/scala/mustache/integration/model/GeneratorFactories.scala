package mustache.integration.model

case class GeneratorFactories(
    serializers: Many[SerializerS],
    isClassloader: Boolean,
    http4sClientTransport: Boolean
)

case class SerializerS(serializer: String, last: Boolean)

object SerializerS:
  def from(s: Seq[String]) =
    if s.isEmpty then throw new IllegalArgumentException("No serializers configured.")
    val l = s.last
    s.map(v => SerializerS(v, v eq l))
