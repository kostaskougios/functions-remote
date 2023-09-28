package functions.discovery.transportscanners

import functions.Log
import functions.discovery.utils.ClassLoaderUtils
import functions.discovery.utils.ClassLoaderUtils.withThreadContextClassLoader
import functions.model.{Coordinates, RuntimeConfig, Transport, TransportFunction}
import functions.serializerscanners.reflectivelyLoadObject

import java.net.{URI, URL, URLClassLoader}
import java.util.function.BiFunction

class SeparateClassLoaderTransport(runtimeConfig: RuntimeConfig) extends TransportScanner:
  override def scan(dependency: String, className: String): TransportFunction =
    Log.info(s"scanning for dependency $dependency")
    val deps        = runtimeConfig.dependenciesFor(dependency)
    Log.info(s"classpath for $dependency is:\n${deps.mkString("\n")}")
    val classLoader = new URLClassLoader(deps.map(p => new URI("file://" + p).toURL), null)
    val biF         = withThreadContextClassLoader(classLoader):
      reflectivelyLoadObject[BiFunction[String, Array[Byte], Array[Byte]]](classLoader, "exported.Exported")
    Log.info(s"Exporter class loaded OK for $dependency")
    (coords, data) =>
      val coordinates = Coordinates(coords)
      Log.info(s"Invoking $coordinates")
      withThreadContextClassLoader(classLoader):
        biF(coords, data)

  override def transport = Transport.SeparateClassLoader
