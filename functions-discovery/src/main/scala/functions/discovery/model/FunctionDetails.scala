package functions.discovery.model

case class FunctionDetails[A](function: A, serializer: Serializer, transport: Transport)
