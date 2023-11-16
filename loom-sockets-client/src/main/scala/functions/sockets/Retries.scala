package functions.sockets

object Retries:
  def retry[R](times: Int, shouldExit: => Boolean)(f: => R): Option[R] =
    try Option(f)
    catch
      case _: Throwable =>
        if times > 0 && !shouldExit then
          Thread.`yield`()
          retry(times - 1, shouldExit)(f)
        else None
