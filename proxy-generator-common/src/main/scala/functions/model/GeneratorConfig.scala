package functions.model

import functions.environment.Env
import functions.utils.FileUtils

import java.io.File

case class GeneratorConfig(configRootDirectory: String):
  val root         = new File(configRootDirectory)
  val local: File  = new File(root, ".local")
  val localExports = new File(local, "exports")

  def exportJar(dep: String): String = {
    val exportFile = new File(localExports, dep + ".export")

    def error = new IllegalStateException(
      s".export file for $dep not found (or empty), this needs to be created after the $dep is published. The export file is needed for the generator to read the tasty files."
    )

    if !exportFile.exists() then throw error
    FileUtils
      .readFile(exportFile)
      .headOption
      .getOrElse(throw error)
      .trim
  }

object GeneratorConfig:
  def withDefaults(configRootDirectory: String = Env.FunctionsHome) = GeneratorConfig(configRootDirectory)
