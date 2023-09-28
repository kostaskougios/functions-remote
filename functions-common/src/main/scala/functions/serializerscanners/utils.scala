package functions.serializerscanners

def reflectivelyLoadObject[F](classLoader: ClassLoader, className: String): F =
  classLoader.loadClass(className + "$").getField("MODULE$").get(null).asInstanceOf[F]
