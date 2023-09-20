package functions.discovery.serializerscanners
import functions.discovery.model.{CallerFactory, Serializer}

class AvroSerializerScanner(classLoader: ClassLoader) extends SerializerScanner:
  override def serializer = Serializer.Avro

  override def scan[A](className: String): Option[CallerFactory[A]] =
    try Some(callerFactoryOf(classLoader, s"${className}CallerAvroSerializedFactory"))
    catch case _: ClassNotFoundException => None
