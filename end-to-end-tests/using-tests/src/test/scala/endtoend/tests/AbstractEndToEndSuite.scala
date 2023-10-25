package endtoend.tests

import functions.model.RuntimeConfig
import functions.transports.SeparateClassLoaderTransport
import org.scalatest.funsuite.AnyFunSuite

import java.io.File

class AbstractEndToEndSuite extends AnyFunSuite:
  def configPath: File =
    val insideIntelliJ = new File("../config")
    if insideIntelliJ.exists then insideIntelliJ
    else new File("end-to-end-tests/config") // sbt

  println(s"config path: $configPath")
  val runtimeConfig        = RuntimeConfig.withDefaults(configRootDirectory = configPath.getAbsolutePath)
  val classLoaderTransport = new SeparateClassLoaderTransport(runtimeConfig)
