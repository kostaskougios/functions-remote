package functions.sockets

import functions.fibers.FiberExecutor
import functions.sockets.internal.{Sender, SocketFiber}

import java.net.{InetAddress, Socket, SocketException}
import java.util.concurrent.{ArrayBlockingQueue, LinkedBlockingQueue}
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*

class SocketPool(inetAddress: InetAddress, port: Int, poolSz: Int, retriesBeforeGivingUp: Int, executor: FiberExecutor):
  private val createdSocketsCounter     = new AtomicLong(0)
  private val invalidatedSocketsCounter = new AtomicLong(0)
  private val queue                     = new LinkedBlockingQueue[Sender](64 * poolSz)
  private val sockets                   =
    for _ <- 1 to poolSz yield new SocketFiber(inetAddress, port, queue, createdSocketsCounter, invalidatedSocketsCounter, retriesBeforeGivingUp, executor)

  def createdSocketsCount: Long     = createdSocketsCounter.incrementAndGet()
  def invalidatedSocketsCount: Long = invalidatedSocketsCounter.incrementAndGet()

  private val correlationId                = new AtomicInteger(0)
  def send(data: Array[Byte]): Array[Byte] =
    val sender = Sender(correlationId.incrementAndGet(), data)
    queue.put(sender)
    sender.response()

  def close(): Unit =
    executor.shutdown()
    for s <- sockets do s.shutdown()

object SocketPool:
  def apply(host: String, port: Int, poolSz: Int = 32, retriesToOpenSocketBeforeGivingUp: Int = 128): SocketPool =
    val inetAddress = InetAddress.getByName(host)
    val executor    = FiberExecutor()
    new SocketPool(inetAddress, port, poolSz, retriesToOpenSocketBeforeGivingUp, executor)
