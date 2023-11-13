package functions.sockets

import java.net.{InetAddress, Socket, SocketException}
import java.util.concurrent.atomic.AtomicInteger

class SocketPool(inetAddress: InetAddress, port: Int, poolSz: Int, retriesToOpenSocketBeforeGivingUp: Int):
  private val available = scala.collection.mutable.Stack.empty[Socket]
  private val used      = new AtomicInteger(0)

  def idleSockets: Seq[Socket] = available.synchronized(available.toList)
  def activeSockets: Int       = used.get()

  private def createSocket(retries: Int): Socket =
    if retries > 0 then
      try new Socket(inetAddress, port)
      catch
        case t: SocketException =>
          Thread.`yield`()
          createSocket(retries - 1)
    else throw new IllegalStateException(s"Can't create socket to $inetAddress:$port")

  def withSocket[R](f: Socket => R): R =
    val s = available.synchronized:
      if available.isEmpty then createSocket(retriesToOpenSocketBeforeGivingUp)
      else available.pop()

    used.incrementAndGet()
    try
      val r = f(s)
      available.synchronized(available.push(s))
      used.decrementAndGet()
      r
    catch
      case t: Throwable =>
        doAndPrintError(s.close())
        used.decrementAndGet()
        withSocket(f)

  def close(): Unit =
    available.synchronized:
      for s <- available do doAndPrintError(s.close())

  private def doAndPrintError[R](f: => Unit): Unit =
    try f
    catch case t: Throwable => t.printStackTrace()
object SocketPool:
  def apply(host: String, port: Int, poolSz: Int = 32, retriesToOpenSocketBeforeGivingUp: Int = 128): SocketPool =
    val inetAddress = InetAddress.getByName(host)
    new SocketPool(inetAddress, port, poolSz, retriesToOpenSocketBeforeGivingUp)
