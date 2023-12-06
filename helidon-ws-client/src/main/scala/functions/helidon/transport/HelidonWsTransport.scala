package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.model.TransportInput
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.WsListener
import jdk.internal.util.ByteArray

import java.io.{ByteArrayOutputStream, DataOutputStream}
import java.util.concurrent.atomic.AtomicLong
import scala.util.Using.Releasable

class HelidonWsTransport(fiberExecutor: FiberExecutor):
  private val wsListener    = new ClientWsListener(fiberExecutor)
  private val correlationId = new AtomicLong(0)

  def clientWsListener: WsListener = wsListener

  def transportFunction(in: TransportInput): Array[Byte] =
    if in.argsData.nonEmpty then
      throw new IllegalArgumentException("argsData has serialized data, did you use the correct helidon factory methods for the caller?")

    val coordsData = in.coordinates4.toRawCoordinatesBytes
    val corId      = correlationId.incrementAndGet()

    val buf = BufferData.growing(in.argsData.length + coordsData.length + 32)
    buf.write(longToBytes(corId))
    buf.writeUnsignedInt32(coordsData.length)
    buf.write(coordsData)
    buf.write(in.data)
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
