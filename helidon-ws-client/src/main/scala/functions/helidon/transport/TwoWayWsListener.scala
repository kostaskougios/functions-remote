package functions.helidon.transport

import functions.fibers.FiberExecutor
import functions.helidon.transport.TwoWayWsListener.PoisonPill
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.{WsListener, WsSession}

import java.io.{ByteArrayInputStream, DataInputStream}
import java.util.concurrent.{CountDownLatch, LinkedBlockingQueue}
import scala.annotation.tailrec
import scala.util.Using.Releasable

class TwoWayWsListener(fiberExecutor: FiberExecutor) extends WsListener:
  private val toSend   = new LinkedBlockingQueue[Array[Byte]](64)
  private val latchMap = collection.concurrent.TrieMap.empty[Long, CountDownLatch]
  private val dataMap  = collection.concurrent.TrieMap.empty[Long, Array[Byte]]

  def send(correlationId: Long, a: Array[Byte]): Array[Byte] =
    toSend.put(a)
    val latch        = new CountDownLatch(1)
    latchMap.put(correlationId, latch)
    latch.await()
    val receivedData = dataMap.getOrElse(correlationId, throw new IllegalStateException(s"No data found for correlationId=$correlationId"))
    latchMap -= correlationId
    dataMap -= correlationId
    receivedData

  override def onMessage(session: WsSession, buffer: BufferData, last: Boolean) =
    val data  = buffer.readBytes()
    val in    = new ByteArrayInputStream(data)
    val din   = new DataInputStream(in)
    val corId = din.readLong()
    latchMap.get(corId) match
      case Some(latch) =>
        dataMap.put(corId, data.drop(4))
        latch.countDown()
      case None        =>
        println(s"Correlation id missing: $corId , received data ignored.")

  override def onOpen(session: WsSession): Unit =
    fiberExecutor.submit:
      @tailrec def cont(): Unit =
        val msg = toSend.take()
        if !msg.eq(PoisonPill) then
          session.send(BufferData.create(msg), true)
          cont()
      cont()

  def close(): Unit =
    toSend.put(PoisonPill)
    for latch <- latchMap.values do latch.countDown()

object TwoWayWsListener:
  val PoisonPill = Array(1.toByte)

  given Releasable[TwoWayWsListener] = twl => twl.close()
