package functions.sockets.internal

import functions.fibers.{Fiber, FiberExecutor}
import functions.lib.logging.Logger
import functions.model.{Coordinates4, ReceiverInput}

import java.io.{DataInputStream, DataOutputStream, EOFException}
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}

class RequestProcessor(
    executor: FiberExecutor,
    in: DataInputStream,
    out: DataOutputStream,
    invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]],
    totalRequestCounter: AtomicLong,
    servingCounter: AtomicInteger,
    logger: Logger
):
  private val queue                              = new LinkedBlockingQueue[Req](1024)
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
      val req = queue.take()
      try
        servingCounter.incrementAndGet()
        val outData = invokerMap(req.coordinates)(ReceiverInput(req.inData))
        out.writeInt(req.correlationId)
        out.writeInt(outData.length)
        out.write(outData)
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
            queue.put(Req(correlationId, coordinates, inData))
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

private case class Req(correlationId: Int, coordinates: Coordinates4, inData: Array[Byte])
