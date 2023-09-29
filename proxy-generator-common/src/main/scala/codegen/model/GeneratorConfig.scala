package codegen.model

import functions.environment.Env
import functions.utils.FileUtils

import java.io.File

case class GeneratorConfig(configRootDirectory: String):
  val root         = new File(configRootDirectory)
  val local: File  = new File(root, ".local")
  val localExports = new File(local, "exports")

  def exportJar(dep: String): String =
    FileUtils
      .readFile(new File(localExports, dep + ".export"))
      .headOption
      .getOrElse(throw new IllegalArgumentException(s".export file for $dep not found or empty"))
      .trim

object GeneratorConfig:
  def withDefaults(configRootDirectory: String = Env.FunctionsHome) = GeneratorConfig(configRootDirectory)
