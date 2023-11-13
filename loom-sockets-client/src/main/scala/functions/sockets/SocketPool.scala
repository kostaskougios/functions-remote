package functions.sockets

import java.net.{InetAddress, Socket}
import java.util.concurrent.atomic.AtomicInteger

class SocketPool(inetAddress: InetAddress, port: Int, poolSz: Int):
  private val available = scala.collection.mutable.Stack.empty[Socket]
  private val used      = new AtomicInteger(0)

  def withSocket[R](f: Socket => R): R =
    val socketO = available.synchronized:
      if available.isEmpty then None else Some(available.pop())

    def callAndKeep(s: Socket): R =
      try
        used.incrementAndGet()
        val r = f(s)
        available.synchronized(available.push(s))
        used.decrementAndGet()
        r
      catch
        case t: Throwable =>
          used.decrementAndGet()
          try s.close()
          catch case ex: Throwable => ex.printStackTrace()
          withSocket(f)

    socketO match
      case Some(s) => callAndKeep(s)
      case None    =>
        while used.get() >= poolSz do Thread.`yield`()
        val s = new Socket(inetAddress, port)
        callAndKeep(s)

  def close(): Unit =
    available.synchronized:
      for s <- available
      do
        try s.close()
        catch case t: Throwable => t.printStackTrace()

object SocketPool:
  def apply(host: String, port: Int, poolSz: Int = 32): SocketPool =
    val inetAddress = InetAddress.getByName(host)
    new SocketPool(inetAddress, port, poolSz)
