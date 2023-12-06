package functions.helidon.ws

import functions.model.{Coordinates4, ReceiverInput}
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.{WsListener, WsSession}

import java.io.{ByteArrayOutputStream, PrintWriter}

class ServerWsListener(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]) extends WsListener:

  override def onMessage(session: WsSession, buffer: BufferData, last: Boolean) =
    val corId          = buffer.readLong()
    val coordsLength   = buffer.readUnsignedInt32()
    val coordsByteData = new Array[Byte](coordsLength.toInt)
    buffer.read(coordsByteData)
    val coordsStr      = new String(coordsByteData, "UTF-8")
    val coordinates4   = Coordinates4.unapply(coordsStr)
    val data           = buffer.readBytes()
    try
      val f        = invokerMap(coordinates4)
      val response = f(ReceiverInput(data))
      val buf      = BufferData.growing(response.length + 8)
      buf.write(0)
      buf.write(longToBytes(corId))
      buf.write(response)
      session.send(buf, true)
    catch
      case t: Throwable =>
        val bos  = new ByteArrayOutputStream
        val w    = new PrintWriter(bos)
        t.printStackTrace(w)
        w.close()
        val data = bos.toByteArray
        val buf  = BufferData.growing(data.length + 8)
        buf.write(1)
        buf.write(longToBytes(corId))
        buf.write(data)
        session.send(buf, true)

  private def longToBytes(x: Long): Array[Byte] =
    val buffer = new Array[Byte](8)
    for (i <- 0 until 8)
      // Shift the long value 8*(7-i) bits to the right and take the lowest 8 bits
      buffer(i) = (x >> (8 * (7 - i))).toByte
    buffer
