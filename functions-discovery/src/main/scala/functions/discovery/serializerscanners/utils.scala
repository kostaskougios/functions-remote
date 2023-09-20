package functions.discovery.serializerscanners

import functions.discovery.model.CallerFactory

def callerFactoryOf[A](classLoader: ClassLoader, className: String): CallerFactory[A] =
  classLoader.loadClass(className + "$").getField("MODULE$").get(null).asInstanceOf[CallerFactory[A]]
