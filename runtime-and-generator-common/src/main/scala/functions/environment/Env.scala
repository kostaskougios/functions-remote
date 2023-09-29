package functions.environment

object Env:
  private val ConfigDir = "functions-remote.config.dir"
  val FunctionsHome     = sys.env.getOrElse(
    ConfigDir,
    throw new IllegalStateException(s"Please set env variable $ConfigDir to point to the config directory of functions-remote")
  )
