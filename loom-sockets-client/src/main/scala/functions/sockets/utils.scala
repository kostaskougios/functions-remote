package functions.sockets

private[sockets] def doAndPrintError(f: => Unit): Unit =
  try f
  catch case t: Throwable => t.printStackTrace()
