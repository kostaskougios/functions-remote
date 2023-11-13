package functions.sockets

import java.net.{InetAddress, Socket, SocketException}

class SocketPool(inetAddress: InetAddress, port: Int, poolSz: Int):
  private val available                = scala.collection.mutable.Stack.empty[Socket]
  def withSocket[R](f: Socket => R): R =
    val socketO = available.synchronized:
      if available.isEmpty then None else Some(available.pop())

    def callAndKeep(s: Socket): R =
      try
        val r = f(s)
        available.synchronized(available.push(s))
        r
      catch
        case t: Throwable =>
          s.close()
          withSocket(f)

    socketO match
      case Some(s) => callAndKeep(s)
      case None    =>
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
