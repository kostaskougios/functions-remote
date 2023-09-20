package functions.discovery.serializerscanners

import functions.discovery.model.{CallerFactory, Serializer}

trait SerializerScanner:
  def scan[A](className: String): Option[CallerFactory[A]]
  def serializer: Serializer
