package functions.model

import functions.environment.RuntimeConfig

case class GeneratorConfig(runtimeConfig: RuntimeConfig):
  def exportJar(dep: String): String =
    runtimeConfig.dependenciesFor(dep).head

object GeneratorConfig:
  def withDefaults(runtimeConfig: RuntimeConfig = RuntimeConfig.withDefaults()) = GeneratorConfig(runtimeConfig)
