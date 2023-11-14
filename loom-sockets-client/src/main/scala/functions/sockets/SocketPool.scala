package functions.sockets

import java.net.{InetAddress, Socket, SocketException}
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*

class SocketPool(inetAddress: InetAddress, port: Int, poolSz: Int, retriesBeforeGivingUp: Int):
  private class Sock(@volatile var socket: Option[Socket] = None):
    def allocateSocket: Socket =
      if socket.isEmpty then
        val s = createSocket(retriesBeforeGivingUp)
        socket = Some(s)
        s
      else socket.get

    def invalidateSocket(): Unit =
      for s <- socket do doAndPrintError(s.close())
      socket = None

    @tailrec private def createSocket(retries: Int): Socket =
      if retries > 0 then
        try
          val s = new Socket(inetAddress, port)
          createdSocketsCounter.incrementAndGet()
          s
        catch
          case _: SocketException =>
            Thread.`yield`()
            createSocket(retries - 1)
      else throw new IllegalStateException(s"Can't create socket to $inetAddress:$port")

  private val createdSocketsCounter = new AtomicLong(0)
  private val available             = new ArrayBlockingQueue[Sock](poolSz)
  for _ <- 1 to poolSz do available.add(Sock())

  def idleSockets: Seq[Socket]  = available.asScala.flatMap(_.socket).toList
  def activeSockets: Int        = poolSz - idleSockets.size
  def createdSocketsCount: Long = createdSocketsCounter.incrementAndGet()

  def withSocket[R](f: Socket => R): R =
    val s = available.take()

    def exec(retries: Int): R =
      if retries < 1 then throw new IllegalStateException(s"Aborting after $retriesBeforeGivingUp retries")
      try f(s.allocateSocket)
      catch
        case _: SocketException =>
          s.invalidateSocket()
          exec(retries - 1)

    try exec(retriesBeforeGivingUp)
    finally available.put(s)

  def close(): Unit =
    available.synchronized:
      for s <- available.asScala do s.invalidateSocket()

  private def doAndPrintError(f: => Unit): Unit =
    try f
    catch case t: Throwable => t.printStackTrace()

object SocketPool:
  def apply(host: String, port: Int, poolSz: Int = 32, retriesToOpenSocketBeforeGivingUp: Int = 128): SocketPool =
    val inetAddress = InetAddress.getByName(host)
    new SocketPool(inetAddress, port, poolSz, retriesToOpenSocketBeforeGivingUp)
