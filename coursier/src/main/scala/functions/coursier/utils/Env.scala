package functions.coursier.utils

object Env {
  private val ConfigDir = "functions_remote_config_dir"
  val UserHome          = sys.props("user.home")
  val FunctionsHome     = sys.env.getOrElse(
    ConfigDir,
    s"$UserHome/.functions-remote-config"
  )
}
