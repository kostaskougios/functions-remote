package functions.transports

import functions.Log
import functions.environment.RuntimeConfig
import functions.model.{TransportFunction, TransportFunctionRaw}
import functions.serializerscanners.reflectivelyLoadObject
import functions.utils.ClassLoaderUtils.withThreadContextClassLoader

import java.net.{URI, URL, URLClassLoader}

/** A transport that uses a separate classloader to fully load a function and all it's dependencies. Then it will send the Array[Byte] serialized function
  * params to it via exported.Exported class which the function's jar should contain.
  * @param runtimeConfig
  *   the RuntimeConfig used to trace the function's jar and dependencies
  */
class IsolatedClassLoaderTransport(runtimeConfig: RuntimeConfig):
  def createTransport(organization: String, exportedArtifact: String, version: String): TransportFunction =
    createTransport(s"$organization:$exportedArtifact:$version")

  def createTransport(dependency: String): TransportFunction =
    Log.info(s"scanning for dependency $dependency")
    val deps        = runtimeConfig.dependenciesFor(dependency)
    Log.info(s"classpath for $dependency is:\n${deps.mkString("\n")}")
    val classLoader = new URLClassLoader(deps.map(p => new URI("file://" + p).toURL), null)
    val biF         = withThreadContextClassLoader(classLoader):
      reflectivelyLoadObject[TransportFunctionRaw](classLoader, "exported.Exported")
    Log.info(s"Exporter class loaded OK for $dependency")
    (coordinates3, data) =>
      Log.info(s"Invoking $coordinates3")
      withThreadContextClassLoader(classLoader):
        biF(coordinates3.toRawCoordinates, data)
