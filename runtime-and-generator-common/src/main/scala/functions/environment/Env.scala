package functions.environment

import functions.Log

object Env:
  // NOTE: any changes should be copied to coursier module's Env class

  private val ConfigDir = "functions_remote_config_dir"
  val UserHome          = sys.props("user.home")
  val FunctionsHome     = sys.env.getOrElse(
    ConfigDir,
    s"$UserHome/.functions-remote-config"
  )
  Log.info(s"functions_remote_config_dir is at $FunctionsHome")
