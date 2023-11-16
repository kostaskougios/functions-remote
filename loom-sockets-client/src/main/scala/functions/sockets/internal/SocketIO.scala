package functions.sockets.internal

import functions.fibers.FiberExecutor
import functions.sockets.{CommonCodes, doAndPrintError}
import functions.sockets.internal.errors.{RemoteMethodThrowedAnException, ShutdownException}

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import java.util.concurrent.BlockingQueue

class SocketIO(socket: Socket, queue: BlockingQueue[Sender], executor: FiberExecutor):
  private val correlationMap = collection.concurrent.TrieMap.empty[Int, Sender]
  private val writerFiber    = executor.submit(writer())
  private val readerFiber    = executor.submit(reader())

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
    try
      val out    = new DataOutputStream(socket.getOutputStream)
      var corrId = 1
      while true do
        val sender        = queue.take()
        val correlationId = corrId
        correlationMap += correlationId -> sender
        out.writeInt(correlationId)
        out.write(sender.data)
        out.flush()
        corrId += 1
    catch
      case t: Throwable =>
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
            in.read() match
              case CommonCodes.ResponseSuccess =>
                val sz   = in.readInt()
                val data = new Array[Byte](sz)
                in.read(data)
                sender.reply(data)
              case CommonCodes.ResponseError   =>
                val sz              = in.readInt()
                val data            = new Array[Byte](sz)
                in.read(data)
                val remoteException = new String(data, "UTF-8")
                sender.fail(new RemoteMethodThrowedAnException(s"The remote method throwed this exception:\n$remoteException"))
            correlationMap -= correlationId
    catch case t: Throwable => invalidate(t)
