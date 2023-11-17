package functions.sockets.internal

import functions.fibers.{Fiber, FiberExecutor}
import functions.lib.logging.Logger
import functions.model.{Coordinates4, ReceiverInput}
import functions.sockets.CommonCodes

import java.io.{ByteArrayOutputStream, DataInputStream, DataOutputStream, EOFException, PrintWriter}
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}

class RequestProcessor(
    executor: FiberExecutor,
    in: DataInputStream,
    out: DataOutputStream,
    invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]],
    totalRequestCounter: AtomicLong,
    servingCounter: AtomicInteger,
    logger: Logger,
    queueSz: Int
):
  private val queue                              = new LinkedBlockingQueue[InvocationOutcome](queueSz)
  @volatile private var readerFiber: Fiber[Unit] = null
  @volatile private var writerFiber: Fiber[Unit] = null

  def shutdown(): Unit =
    readerFiber.interrupt()
    writerFiber.interrupt()

  def serve(): Unit =
    readerFiber = executor.submit(reader())
    writerFiber = executor.submit(writer())
    readerFiber.await()
    writerFiber.await()

  private def writer(): Unit =
    while true do
      if queue.size() > queueSz - 10 then
        logger.warn(s"per-stream-queue almost full, size = ${queue.size()} out of $queueSz. Increase perStreamQueueSz or decrease blocking calls per fiber?")
      val req = queue.take()
      try
        servingCounter.incrementAndGet()
        out.writeInt(req.correlationId)
        req match
          case InvocationSuccess(_, outData) =>
            out.write(CommonCodes.ResponseSuccess)
            out.writeInt(outData.length)
            out.write(outData)
          case f: InvocationFailure          =>
            out.write(CommonCodes.ResponseError)
            val errorData = f.exceptionToByteArray
            out.writeInt(errorData.length)
            out.write(errorData)
        out.flush()
      catch
        case t: Throwable =>
          logger.error(t)
          shutdown()
      finally servingCounter.decrementAndGet()

  private def reader(): Unit =
    try
      while true do
        in.readInt() match
          case 0             =>
            throw new IllegalStateException("Incorrect data on the socket (maybe from the client)")
          case correlationId =>
            totalRequestCounter.incrementAndGet()
            val coordsSz    = in.readInt()
            val coordsRaw   = new String(in.readNBytes(coordsSz), "UTF-8")
            val coordinates = Coordinates4(coordsRaw)
            val inData      = inputStreamToByteArray(in)
            executor.submit:
              try
                val outData = invokerMap(coordinates)(ReceiverInput(inData))
                queue.put(InvocationSuccess(correlationId, outData))
              catch
                case t: Throwable =>
                  queue.put(InvocationFailure(correlationId, t))
    catch
      case _: EOFException => shutdown()
      case t: Throwable    =>
        logger.error(t)
        shutdown()

  private def inputStreamToByteArray(in: DataInputStream): Array[Byte] =
    val dataSz = in.readInt()
    val data   = new Array[Byte](dataSz)
    in.read(data)
    data

private sealed trait InvocationOutcome:
  def correlationId: Int
private case class InvocationSuccess(correlationId: Int, outData: Array[Byte]) extends InvocationOutcome
private case class InvocationFailure(correlationId: Int, error: Throwable)     extends InvocationOutcome:
  def exceptionToByteArray: Array[Byte] =
    val bos = new ByteArrayOutputStream(8192)
    val pw  = new PrintWriter(bos)
    error.printStackTrace(pw)
    pw.flush()
    bos.toByteArray
