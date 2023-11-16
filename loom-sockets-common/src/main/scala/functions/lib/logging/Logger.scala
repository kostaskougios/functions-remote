package functions.lib.logging

trait Logger:
  def error(t: Throwable): Unit
  def warn(msg: String): Unit

object Logger:
  val Console = new Logger:
    override def warn(msg: String): Unit   = println(msg)
    override def error(t: Throwable): Unit = t.printStackTrace()
