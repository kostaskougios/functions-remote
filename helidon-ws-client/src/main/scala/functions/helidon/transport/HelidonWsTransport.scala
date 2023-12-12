package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.helidon.ws.InOutMessageProtocol
import functions.model.TransportInput
import io.helidon.websocket.WsListener

import java.util.concurrent.atomic.AtomicLong
import scala.util.Random
import scala.util.Using.Releasable

class HelidonWsTransport(fiberExecutor: FiberExecutor, sendResponseTimeoutInMillis: Long):
  private val myId          = Random.nextInt()
  private val wsListener    = new ClientWsListener(myId, fiberExecutor, sendResponseTimeoutInMillis)
  private val correlationId = new AtomicLong(0)
  private val protocol      = new InOutMessageProtocol(Map.empty)

  def clientWsListener: WsListener = wsListener

  def transportFunction(in: TransportInput): Array[Byte] =
    val coordsData = in.coordinates4.toRawCoordinatesBytes
    val corId      = correlationId.incrementAndGet()
    val buf        = protocol.transport(myId, corId, in.data, in.argsData, coordsData)
    wsListener.send(corId, in.coordinates4, buf)

  def close(): Unit = wsListener.close()

object HelidonWsTransport:
  given Releasable[HelidonWsTransport] = _.close()
