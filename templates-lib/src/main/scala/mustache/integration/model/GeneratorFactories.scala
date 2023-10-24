package mustache.integration.model

case class GeneratorFactories(
    serializers: Many[String],
    isHttp4s: Boolean
)
