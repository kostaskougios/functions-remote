package functions.sockets

import java.net.{InetAddress, Socket, SocketException}
import java.util.concurrent.atomic.AtomicInteger

class SocketPool(inetAddress: InetAddress, port: Int, poolSz: Int, retriesToOpenSocketBeforeGivingUp: Int):
  private val available = scala.collection.mutable.Stack.empty[Socket]
  private val used      = new AtomicInteger(0)

  def idleSockets: Seq[Socket] = available.synchronized(available.toList)
  def activeSockets: Int       = used.get()

  def withSocket[R](f: Socket => R): R =
    val socketO = available.synchronized:
      if available.isEmpty then None else Some(available.pop())

    def callAndKeep(s: Socket): R =
      used.incrementAndGet()
      try
        val r = f(s)
        available.synchronized(available.push(s))
        used.decrementAndGet()
        r
      catch
        case t: Throwable =>
          try s.close()
          catch case ex: Throwable => ex.printStackTrace()
          used.decrementAndGet()
          withSocket(f)

    def createSocket(retries: Int): Socket =
      if retries > 0 then
        waitIfPoolIsFull()
        try new Socket(inetAddress, port)
        catch
          case t: SocketException =>
            Thread.`yield`()
            createSocket(retries - 1)
      else throw new IllegalStateException(s"Can't create socket to $inetAddress:$port")

    socketO match
      case Some(s) => callAndKeep(s)
      case None    =>
        val s = createSocket(retriesToOpenSocketBeforeGivingUp)
        callAndKeep(s)

  private def waitIfPoolIsFull(): Unit = while used.get() >= poolSz do Thread.`yield`()

  def close(): Unit =
    available.synchronized:
      for s <- available
      do
        try s.close()
        catch case t: Throwable => t.printStackTrace()

object SocketPool:
  def apply(host: String, port: Int, poolSz: Int = 32, retriesToOpenSocketBeforeGivingUp: Int = 128): SocketPool =
    val inetAddress = InetAddress.getByName(host)
    new SocketPool(inetAddress, port, poolSz, retriesToOpenSocketBeforeGivingUp)
