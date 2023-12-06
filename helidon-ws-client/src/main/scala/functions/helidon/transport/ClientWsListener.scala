package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.helidon.transport.ClientWsListener.PoisonPill
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.{WsListener, WsSession}

import java.util.concurrent.{CountDownLatch, LinkedBlockingQueue}
import scala.annotation.tailrec
import scala.util.Using.Releasable

class ClientWsListener(fiberExecutor: FiberExecutor) extends WsListener:
  private val toSend   = new LinkedBlockingQueue[BufferData](64)
  private val latchMap = collection.concurrent.TrieMap.empty[Long, CountDownLatch]
  private val dataMap  = collection.concurrent.TrieMap.empty[Long, Array[Byte]]

  def send(correlationId: Long, a: BufferData): Array[Byte] =
    toSend.put(a)
    val latch        = new CountDownLatch(1)
    latchMap.put(correlationId, latch)
    latch.await()
    val receivedData = dataMap.getOrElse(correlationId, throw new IllegalStateException(s"No data found for correlationId=$correlationId"))
    latchMap -= correlationId
    dataMap -= correlationId
    receivedData

  override def onMessage(session: WsSession, buffer: BufferData, last: Boolean): Unit =
    val corId = buffer.readLong()
    val data  = buffer.readBytes()
    latchMap.get(corId) match
      case Some(latch) =>
        dataMap.put(corId, data)
        latch.countDown()
      case None        =>
        println(s"Correlation id missing: $corId , received data ignored.")

  override def onOpen(session: WsSession): Unit =
    fiberExecutor.submit:
      @tailrec def cont(): Unit =
        val msg = toSend.take()
        if !msg.eq(PoisonPill) then
          session.send(msg, true)
          cont()
      cont()

  def close(): Unit =
    toSend.put(PoisonPill)
    for latch <- latchMap.values do latch.countDown()

object ClientWsListener:
  private val PoisonPill = BufferData.create("*poisonpill*")

  given Releasable[ClientWsListener] = twl => twl.close()
