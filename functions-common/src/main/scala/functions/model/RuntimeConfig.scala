package functions.model

import functions.environment.Env

case class RuntimeConfig(
    classLoader: ClassLoader,
    configRootDirectory: String
)

object RuntimeConfig:
  def withDefaults(classLoader: ClassLoader = Thread.currentThread().getContextClassLoader, configRootDirectory: String = Env.FunctionsHome): RuntimeConfig =
    RuntimeConfig(classLoader, configRootDirectory)
