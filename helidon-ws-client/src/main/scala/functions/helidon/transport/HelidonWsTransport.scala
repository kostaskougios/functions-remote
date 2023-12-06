package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.model.TransportInput
import io.helidon.websocket.WsListener

import java.io.{ByteArrayOutputStream, DataOutputStream}
import java.util.concurrent.atomic.AtomicLong
import scala.util.Using.Releasable

class HelidonWsTransport(fiberExecutor: FiberExecutor):
  private val twl           = new TwoWayWsListener(fiberExecutor)
  private val correlationId = new AtomicLong(0)

  def wsListener: WsListener = twl

  def transportFunction(in: TransportInput): Array[Byte] =
    if in.argsData.nonEmpty then
      throw new IllegalArgumentException("argsData has serialized data, did you use the correct helidon factory methods for the caller?")

    val coordsData = in.coordinates4.toRawCoordinatesBytes
    val corId      = correlationId.incrementAndGet()

    val bos  = new ByteArrayOutputStream(in.argsData.length + coordsData.length + 32)
    val dos  = new DataOutputStream(bos)
    dos.writeLong(corId)
    dos.writeInt(coordsData.length)
    dos.write(coordsData)
    dos.write(in.data)
    dos.close()
    val data = bos.toByteArray
    twl.send(corId, data)

  def close(): Unit = twl.close()

object HelidonWsTransport:
  given Releasable[HelidonWsTransport] = _.close()
