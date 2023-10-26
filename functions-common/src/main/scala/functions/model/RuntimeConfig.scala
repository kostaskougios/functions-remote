package functions.model

import functions.environment.Env
import functions.utils.FileUtils

import java.io.File

case class RuntimeConfig(
    classLoader: ClassLoader,
    configRootDirectory: String
):
  val local: File                                 = new File(configRootDirectory, ".local")
  val localDependencies: File                     = new File(local, "dependencies")
  def dependenciesFor(dep: String): Array[String] = FileUtils.readFile(new File(localDependencies, dep + ".classpath")).toArray

object RuntimeConfig:
  def withDefaults(classLoader: ClassLoader = Thread.currentThread().getContextClassLoader, configRootDirectory: String = Env.FunctionsHome): RuntimeConfig =
    RuntimeConfig(classLoader, configRootDirectory)
