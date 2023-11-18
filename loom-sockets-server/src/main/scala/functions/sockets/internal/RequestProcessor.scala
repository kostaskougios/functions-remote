package functions.sockets.internal

import functions.fibers.{Fiber, FiberExecutor, TwoFibers}
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
    queueSz: Int,
    protocol: RequestProtocol
):
  private val queue = new LinkedBlockingQueue[InvocationOutcome](queueSz)

  def serve(): Unit =
    executor.two(reader, writer).await()

  private def writer(fibers: TwoFibers[Unit, Unit]): Unit =
    while true do
      if queue.size() > queueSz - 10 then
        logger.warn(s"per-stream-queue almost full, size = ${queue.size()} out of $queueSz. Increase perStreamQueueSz or decrease blocking calls per fiber?")
      val req = queue.take()
      try
        servingCounter.incrementAndGet()
        out.writeInt(req.correlationId)
        protocol.writer(req, out)
        out.flush()
      catch
        case t: Throwable =>
          logger.error(t)
          fibers.interrupt()
      finally servingCounter.decrementAndGet()

  private def reader(fibers: TwoFibers[Unit, Unit]): Unit =
    try
      while true do
        in.readInt() match
          case 0             =>
            throw new IllegalStateException("Incorrect data on the socket (maybe from the client)")
          case correlationId =>
            totalRequestCounter.incrementAndGet()
            val (coordinates, inData) = protocol.reader(in)
            executor.submit:
              try
                val outData = invokerMap(coordinates)(ReceiverInput(inData))
                queue.put(InvocationSuccess(correlationId, outData))
              catch
                case t: Throwable =>
                  queue.put(InvocationFailure(correlationId, t))
    catch
      case _: EOFException => fibers.interrupt()
      case t: Throwable    =>
        logger.error(t)
        fibers.interrupt()

sealed trait InvocationOutcome:
  def correlationId: Int
case class InvocationSuccess(correlationId: Int, outData: Array[Byte]) extends InvocationOutcome
case class InvocationFailure(correlationId: Int, error: Throwable)     extends InvocationOutcome:
  def exceptionToByteArray: Array[Byte] =
    val bos = new ByteArrayOutputStream(8192)
    val pw  = new PrintWriter(bos)
    error.printStackTrace(pw)
    pw.flush()
    bos.toByteArray
