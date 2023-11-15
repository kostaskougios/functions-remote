package functions.sockets

object Retries:
  def retry[R](times: Int)(f: => R): Option[R] =
    try Some(f)
    catch
      case _: Throwable =>
        if times > 0 then
          Thread.`yield`()
          retry(times - 1)(f)
        else None
