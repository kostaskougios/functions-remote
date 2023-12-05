package functions.model

import functions.environment.RuntimeConfig

case class GeneratorConfig(runtimeConfig: RuntimeConfig):
  def exportJar(dep: String): Array[String] =
    runtimeConfig.dependenciesFor(dep)

object GeneratorConfig:
  def withDefaults(runtimeConfig: RuntimeConfig = RuntimeConfig.withDefaults()) = GeneratorConfig(runtimeConfig)
