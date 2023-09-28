package functions.serializerscanners

import functions.model.Serializer

class GenericScanner[F](classLoader: ClassLoader, val serializer: Serializer, suffix: String) extends SerializerScanner[F]:
  override def scan(className: String): Option[F] =
    try Some(reflectivelyLoadObject(classLoader, s"$className$suffix"))
    catch case _: ClassNotFoundException => None
