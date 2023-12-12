package functions.helidon.ws.transport

import functions.fibers.FiberExecutor
import ClientServerWsListener.PoisonPill
import functions.helidon.ws.InOutMessageProtocol
import functions.helidon.ws.model.RfWsResponse
import functions.helidon.ws.transport.exceptions.RemoteFunctionFailedException
import functions.model.Coordinates4
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.{WsListener, WsSession}

import java.util.concurrent.{CountDownLatch, LinkedBlockingQueue, TimeUnit}
import scala.annotation.tailrec
import scala.util.Using.Releasable

class ClientServerWsListener(protocol: InOutMessageProtocol, fiberExecutor: FiberExecutor, sendResponseTimeoutInMillis: Long) extends WsListener:
  private val toSend   = new LinkedBlockingQueue[BufferData](64)
  private val latchMap = collection.concurrent.TrieMap.empty[Long, CountDownLatch]
  private val dataMap  = collection.concurrent.TrieMap.empty[Long, (Int, Array[Byte])]

  def send(correlationId: Long, coordinates4: Coordinates4, buf: BufferData): Array[Byte] =
    val latch = new CountDownLatch(1)
    latchMap.put(correlationId, latch)
    toSend.put(buf)
    try
      latch.await(sendResponseTimeoutInMillis, TimeUnit.MILLISECONDS)
      val (result, receivedData) = dataMap.getOrElse(
        correlationId,
        throw new IllegalStateException(
          s"No data found for correlationId=$correlationId after waiting for a response for $sendResponseTimeoutInMillis millis for coordinates $coordinates4"
        )
      )
      if result == 0 then receivedData
      else throw new RemoteFunctionFailedException(new String(receivedData, "UTF-8"))
    finally
      latchMap -= correlationId
      dataMap -= correlationId

  override def onMessage(session: WsSession, buffer: BufferData, last: Boolean): Unit =
    try
      protocol.listener(buffer) match
        case Left(out)                                =>
          // act as a server: do a call
          session.send(out, true)
        case Right(RfWsResponse(result, corId, data)) =>
          // act as a client: respond to a call
          latchMap.get(corId) match
            case Some(latch) =>
              dataMap.put(corId, (result, data))
              latch.countDown()
            case None        =>
              println(s"Correlation id missing: $corId , received data ignored.")
    catch case t: Throwable => t.printStackTrace()

  override def onOpen(session: WsSession): Unit =
    fiberExecutor.submit:
      @tailrec def cont(): Unit =
        val msg = toSend.take()
        if !msg.eq(PoisonPill) then
          session.send(msg, true)
          cont()
        else session.terminate()
      cont()

  def close(): Unit =
    toSend.put(PoisonPill)
    for latch <- latchMap.values do latch.countDown()

object ClientServerWsListener:
  private val PoisonPill = BufferData.create("*poisonpill*")

  given Releasable[ClientServerWsListener] = _.close()
