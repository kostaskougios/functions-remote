package functions

object Log:
  private val isLoggingEnabled = sys.props.contains("functions.debug")
  def info(s: => String): Unit = if isLoggingEnabled then println(s)
