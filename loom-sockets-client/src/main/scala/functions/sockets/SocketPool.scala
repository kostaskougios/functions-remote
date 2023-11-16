package functions.sockets

import functions.fibers.FiberExecutor
import functions.sockets.internal.{Sender, SocketFiber}

import java.net.InetAddress
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong
import scala.util.Using.Releasable

class SocketPool(host: String, port: Int, executor: FiberExecutor, poolSz: Int = 32, retriesBeforeGivingUp: Int = 128):
  private val createdSocketsCounter     = new AtomicLong(0)
  private val invalidatedSocketsCounter = new AtomicLong(0)
  private val queue                     = new LinkedBlockingQueue[Sender](64 * poolSz)
  private val sockets                   =
    for _ <- 1 to poolSz
    yield new SocketFiber(InetAddress.getByName(host), port, queue, createdSocketsCounter, invalidatedSocketsCounter, retriesBeforeGivingUp, executor)

  def createdSocketsCount: Long     = createdSocketsCounter.incrementAndGet()
  def invalidatedSocketsCount: Long = invalidatedSocketsCounter.incrementAndGet()

  def send(data: Array[Byte]): Array[Byte] =
    val sender = new Sender(data)
    queue.put(sender)
    sender.response()

  def shutdown(): Unit =
    for s <- sockets do s.shutdown()
    queue.clear()

object SocketPool:
  given Releasable[SocketPool] = pool => pool.shutdown()
