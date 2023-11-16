package functions.lib.logging

trait Logger:
  def error(t: Throwable): Unit

object Logger:
  val Console = new Logger:
    override def error(t: Throwable): Unit = t.printStackTrace()
