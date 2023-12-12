package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.helidon.ws.InOutMessageProtocol
import functions.helidon.ws.transport.ClientWsListener
import functions.model.TransportInput
import io.helidon.websocket.WsListener

import java.util.concurrent.atomic.AtomicLong
import scala.util.Using.Releasable

class HelidonWsTransport(fiberExecutor: FiberExecutor, sendResponseTimeoutInMillis: Long):
  private val protocol      = new InOutMessageProtocol(Map.empty)
  private val wsListener    = new ClientWsListener(protocol, fiberExecutor, sendResponseTimeoutInMillis)
  private val correlationId = new AtomicLong(0)

  def clientWsListener: WsListener = wsListener

  def transportFunction(in: TransportInput): Array[Byte] =
    val coordsData = in.coordinates4.toRawCoordinatesBytes
    val corId      = correlationId.incrementAndGet()
    val buf        = protocol.clientTransport(corId, in.data, in.argsData, coordsData)
    wsListener.send(corId, in.coordinates4, buf)

  def close(): Unit = wsListener.close()

object HelidonWsTransport:
  given Releasable[HelidonWsTransport] = _.close()
