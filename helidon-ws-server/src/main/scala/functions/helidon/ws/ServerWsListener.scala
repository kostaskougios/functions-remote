package functions.helidon.ws

import functions.model.InvokerMap
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.{WsListener, WsSession}

class ServerWsListener(invokerMap: InvokerMap) extends WsListener:

  private val protocol = new InOutMessageProtocol(invokerMap)

  override def onMessage(session: WsSession, buffer: BufferData, last: Boolean): Unit =
    val out = protocol.listener(buffer)
    session.send(out, true)
