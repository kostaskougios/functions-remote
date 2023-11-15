package functions.sockets.internal

import functions.fibers.FiberExecutor
import functions.sockets.doAndPrintError
import functions.sockets.internal.errors.NoMoreDataFromServerException

import java.net.Socket
import java.util.concurrent.BlockingQueue

class SocketInOut(socket: Socket, queue: BlockingQueue[Sender], executor: FiberExecutor):
  private val writerFiber    = executor.submit(writer())
  private val readerFiber    = executor.submit(reader())
  private val correlationMap = collection.concurrent.TrieMap.empty[Int, Sender]

  def waitTillDone(): Unit =
    writerFiber.get()
    readerFiber.get()

  private def invalidate(t: Throwable): Unit =
    doAndPrintError(socket.close())
    writerFiber.interrupt()
    readerFiber.interrupt()
    for s <- correlationMap.values do s.fail(t)

  private def writer(): Unit =
    try
      val out = socket.getOutputStream
      while true do
        val sender = queue.take()
        println(s"${Thread.currentThread()} got ${sender.correlationId}")
        correlationMap += sender.correlationId -> sender
        out.write(sender.correlationId)
        out.write(sender.data)
    catch case t: Throwable => invalidate(t)

  private def reader(): Unit =
    try
      val in = socket.getInputStream
      while true do
        in.read() match
          case -1            =>
            invalidate(new NoMoreDataFromServerException)
          case correlationId =>
            val sender = correlationMap(correlationId)
            val sz     = in.read()
            val data   = new Array[Byte](sz)
            in.read(data)
            sender.reply(data)
    catch case t: Throwable => invalidate(t)
