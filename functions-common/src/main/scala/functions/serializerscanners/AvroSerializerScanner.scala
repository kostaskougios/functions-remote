package functions.serializerscanners

import functions.model.Serializer

class AvroSerializerScanner[F](classLoader: ClassLoader) extends SerializerScanner[F]:
  override def serializer = Serializer.Avro

  override def scan(className: String): Option[F] =
    try Some(callerFactoryOf(classLoader, s"${className}CallerAvroSerializedFactory"))
    catch case _: ClassNotFoundException => None
