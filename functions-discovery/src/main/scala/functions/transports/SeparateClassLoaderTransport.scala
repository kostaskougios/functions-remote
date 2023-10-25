package functions.transports

import functions.Log
import functions.discovery.utils.ClassLoaderUtils
import functions.discovery.utils.ClassLoaderUtils.withThreadContextClassLoader
import functions.model.{Coordinates3, RuntimeConfig, Serializer, TransportFunction, TransportFunctionRaw}
import functions.serializerscanners.reflectivelyLoadObject

import java.net.{URI, URL, URLClassLoader}

class SeparateClassLoaderTransport(runtimeConfig: RuntimeConfig):
  def createTransport(dependency: String, serializer: Serializer): TransportFunction =
    Log.info(s"scanning for dependency $dependency")
    val deps        = runtimeConfig.dependenciesFor(dependency)
    Log.info(s"classpath for $dependency is:\n${deps.mkString("\n")}")
    val classLoader = new URLClassLoader(deps.map(p => new URI("file://" + p).toURL), null)
    val biF         = withThreadContextClassLoader(classLoader):
      reflectivelyLoadObject[TransportFunctionRaw](classLoader, "exported.Exported")
    Log.info(s"Exporter class loaded OK for $dependency")
    (coordinates2, data) =>
      Log.info(s"Invoking $coordinates2")
      withThreadContextClassLoader(classLoader):
        val coordinates3 = Coordinates3(coordinates2, serializer)
        biF(coordinates3.toRawCoordinates, data)
