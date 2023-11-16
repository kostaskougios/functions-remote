package functions.sockets.internal

import functions.fibers.FiberExecutor
import functions.sockets.doAndPrintError
import functions.sockets.internal.errors.ShutdownException

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import java.util.concurrent.BlockingQueue

class SocketIO(socket: Socket, queue: BlockingQueue[Sender], executor: FiberExecutor):
  private val writerFiber    = executor.submit(writer())
  private val readerFiber    = executor.submit(reader())
  private val correlationMap = collection.concurrent.TrieMap.empty[Int, Sender]

  def shutdown(): Unit =
    invalidate(new ShutdownException)

  def waitTillDone(): Unit =
    writerFiber.get()
    readerFiber.get()

  private def invalidate(t: Throwable): Unit =
    writerFiber.interrupt()
    doAndPrintError(socket.close())
    writerFiber.await()
    readerFiber.await()
    for s <- correlationMap.values do s.fail(t)

  private def writer(): Unit =
    var sender: Sender = null
    try
      val out    = new DataOutputStream(socket.getOutputStream)
      var corrId = 1
      while true do
        sender = queue.take()
        val correlationId = corrId
        // weird issue: when interrupted, correlationMap is null
        if correlationMap == null then throw ShutdownException()
        correlationMap += correlationId -> sender
        out.writeInt(correlationId)
        out.write(sender.data)
        out.flush()
        corrId += 1
    catch
      case t: Throwable =>
        if sender != null then sender.fail(t)
        invalidate(t)

  private def reader(): Unit =
    try
      val in = new DataInputStream(socket.getInputStream)
      while true do
        in.readInt() match
          case 0             =>
            throw new IllegalStateException("Incorrect data on the socket (maybe from the server)")
          case correlationId =>
            val sender = correlationMap.get(correlationId) match
              case Some(id) => id
              case None => throw new NoSuchElementException(s"Correlation id $correlationId not found in ${correlationMap.keys.toList.sorted.mkString(", ")}")
            val sz     = in.readInt()
            val data   = new Array[Byte](sz)
            in.read(data)
            sender.reply(data)
            correlationMap -= correlationId
    catch case t: Throwable => invalidate(t)
