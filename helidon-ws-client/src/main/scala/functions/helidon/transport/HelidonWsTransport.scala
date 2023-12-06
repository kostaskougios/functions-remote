package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.model.TransportInput
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.WsListener

import java.util.concurrent.atomic.AtomicLong
import scala.util.Using.Releasable

class HelidonWsTransport(fiberExecutor: FiberExecutor, sendResponseTimeoutInMillis: Long):
  private val wsListener    = new ClientWsListener(fiberExecutor, sendResponseTimeoutInMillis)
  private val correlationId = new AtomicLong(0)

  def clientWsListener: WsListener = wsListener

  def transportFunction(in: TransportInput): Array[Byte] =
    val coordsData = in.coordinates4.toRawCoordinatesBytes
    val corId      = correlationId.incrementAndGet()

    val buf = BufferData.growing(in.data.length + in.argsData.length + coordsData.length + 32)
    buf.write(longToBytes(corId))
    buf.writeUnsignedInt32(coordsData.length)
    buf.write(coordsData)
    buf.writeInt32(in.data.length)
    buf.write(in.data)
    buf.writeInt32(in.argsData.length)
    buf.write(in.argsData)
    wsListener.send(corId, buf)

  def close(): Unit = wsListener.close()

  private def longToBytes(x: Long): Array[Byte] =
    val buffer = new Array[Byte](8)
    for (i <- 0 until 8)
      // Shift the long value 8*(7-i) bits to the right and take the lowest 8 bits
      buffer(i) = (x >> (8 * (7 - i))).toByte
    buffer

object HelidonWsTransport:
  given Releasable[HelidonWsTransport] = _.close()
