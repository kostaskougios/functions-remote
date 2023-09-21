package functions.serializerscanners

import functions.model.{CallerFactory, Serializer}

trait SerializerScanner[F]:
  def scan(className: String): Option[F]
  def serializer: Serializer
