package functions.sockets

import java.net.{InetAddress, Socket, SocketException}
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}
import java.util.concurrent.atomic.AtomicInteger
import scala.jdk.CollectionConverters.*

class SocketPool(inetAddress: InetAddress, port: Int, poolSz: Int, retriesToOpenSocketBeforeGivingUp: Int):
  private class Sock(@volatile var socket: Option[Socket] = None):
    def allocateSocket: Socket =
      if socket.isEmpty then
        val s = createSocket(retriesToOpenSocketBeforeGivingUp)
        socket = Some(s)
        s
      else socket.get

    def invalidateSocket(): Unit =
      for s <- socket do doAndPrintError(s.close())
      socket = None

    private def createSocket(retries: Int): Socket =
      if retries > 0 then
        try new Socket(inetAddress, port)
        catch
          case t: SocketException =>
            Thread.`yield`()
            createSocket(retries - 1)
      else throw new IllegalStateException(s"Can't create socket to $inetAddress:$port")

  private val available = new ArrayBlockingQueue[Sock](poolSz)
  for _ <- 1 to poolSz do available.add(Sock())

  def idleSockets: Seq[Socket] = available.asScala.flatMap(_.socket).toList
  def activeSockets: Int       = poolSz - idleSockets.size

  def withSocket[R](f: Socket => R): R =
    val s = available.take()
    try f(s.allocateSocket)
    catch
      case _: SocketException =>
        s.invalidateSocket()
        available.put(s)
        withSocket(f)
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
