package endtoend.tests

import functions.discovery.FunctionsDiscovery
import functions.model.RuntimeConfig
import org.scalatest.funsuite.AnyFunSuite

import java.io.File

class AbstractEndToEndSuite extends AnyFunSuite:
  def configPath = new File("../config")
  println(s"config path: $configPath")
  val discovery  = FunctionsDiscovery(RuntimeConfig.withDefaults(configRootDirectory = configPath.getAbsolutePath))
