package functions.discovery.scanners
import functions.discovery.model.{CallerFactory, Serializer}

class AvroScanner(classLoader: ClassLoader) extends Scanner:
  override def serializer = Serializer.Avro

  override def scan[A](className: String): Option[CallerFactory[A]] =
    try Some(callerFactoryOf(classLoader, s"${className}CallerAvroSerializedFactory"))
    catch case _: ClassNotFoundException => None
