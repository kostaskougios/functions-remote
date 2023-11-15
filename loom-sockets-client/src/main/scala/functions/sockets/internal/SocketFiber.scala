package functions.sockets.internal

import functions.fibers.FiberExecutor
import functions.sockets.{Retries, doAndPrintError}

import java.net.{InetAddress, Socket}
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

class SocketFiber(
    inetAddress: InetAddress,
    port: Int,
    queue: LinkedBlockingQueue[Sender],
    createdSocketsCounter: AtomicLong,
    invalidatedSocketsCounter: AtomicLong,
    retriesBeforeGivingUp: Int,
    executor: FiberExecutor
):
  private val running          = new AtomicBoolean(true)
  @volatile private var socket = Option.empty[Socket]

  processRequests()

  def processRequests(): Unit =
    executor.submit:
      while running.get() do
        socket = createSocket
        socket match {
          case None         => Thread.sleep(1)
          case Some(socket) =>
            val inOut = new SocketInOut(socket, queue, executor)
            inOut.waitTillDone()
            println("Socket invalidated")
            invalidatedSocketsCounter.incrementAndGet()
        }

  private def createSocket: Option[Socket] =
    Retries.retry(retriesBeforeGivingUp):
      val s = new Socket(inetAddress, port)
      createdSocketsCounter.incrementAndGet()
      s

  def shutdown(): Unit =
    running.set(false)
    doAndPrintError(socket.foreach(_.close()))
