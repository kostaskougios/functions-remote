package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.helidon.transport.ClientWsListener.PoisonPill
import functions.helidon.transport.exceptions.RemoteFunctionFailedException
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.{WsListener, WsSession}

import java.util.concurrent.{CountDownLatch, LinkedBlockingQueue, TimeUnit}
import scala.annotation.tailrec
import scala.util.Using.Releasable

class ClientWsListener(fiberExecutor: FiberExecutor, sendResponseTimeoutInMillis: Long) extends WsListener:
  private val toSend   = new LinkedBlockingQueue[BufferData](64)
  private val latchMap = collection.concurrent.TrieMap.empty[Long, CountDownLatch]
  private val dataMap  = collection.concurrent.TrieMap.empty[Long, (Int, Array[Byte])]

  def send(correlationId: Long, a: BufferData): Array[Byte] =
    toSend.put(a)
    val latch                  = new CountDownLatch(1)
    latchMap.put(correlationId, latch)
    latch.await(sendResponseTimeoutInMillis, TimeUnit.MILLISECONDS)
    val (result, receivedData) = dataMap.getOrElse(
      correlationId,
      throw new IllegalStateException(s"No data found for correlationId=$correlationId after waiting for a response for $sendResponseTimeoutInMillis millis")
    )
    latchMap -= correlationId
    dataMap -= correlationId
    if result == 0 then receivedData
    else throw new RemoteFunctionFailedException(new String(receivedData, "UTF-8"))

  override def onMessage(session: WsSession, buffer: BufferData, last: Boolean): Unit =
    val result = buffer.read()
    val corId  = buffer.readLong()
    val data   = buffer.readBytes()
    latchMap.get(corId) match
      case Some(latch) =>
        dataMap.put(corId, (result, data))
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
