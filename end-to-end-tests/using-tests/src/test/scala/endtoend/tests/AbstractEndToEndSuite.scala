package endtoend.tests

import functions.environment.RuntimeConfig
import org.scalatest.funsuite.AnyFunSuite

class AbstractEndToEndSuite extends AnyFunSuite:
  val runtimeConfig = RuntimeConfig.withDefaults()
