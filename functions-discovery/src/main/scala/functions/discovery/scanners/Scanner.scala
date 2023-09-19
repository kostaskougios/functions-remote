package functions.discovery.scanners

import functions.discovery.model.{CallerFactory, Serializer}

trait Scanner:
  def scan[A](className: String): Option[CallerFactory[A]]
  def serializer: Serializer
