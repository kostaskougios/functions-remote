package functions.discovery.scanners

import functions.discovery.model.CallerFactory

def callerFactoryOf[A](classLoader: ClassLoader, className: String): CallerFactory[A] =
  classLoader.loadClass(className + "$").getField("MODULE$").get(null).asInstanceOf[CallerFactory[A]]
