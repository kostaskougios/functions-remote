package functions.serializerscanners

import functions.model.CallerFactory

def callerFactoryOf[F](classLoader: ClassLoader, className: String): F =
  classLoader.loadClass(className + "$").getField("MODULE$").get(null).asInstanceOf[F]
