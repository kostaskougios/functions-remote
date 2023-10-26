package mustache.integration.model

case class GeneratorFactories(
    serializers: Many[SerializerS],
    isClassloader: Boolean,
    isHttp4s: Boolean
)

case class SerializerS(serializer: String, last: Boolean)

object SerializerS:
  def from(s: Seq[String]) =
    val l = s.last
    s.map(v => SerializerS(v, v eq l))
