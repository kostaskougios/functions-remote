package functions.helidon.ws

import functions.model.{Coordinates4, ReceiverInput}
import io.helidon.common.buffers.BufferData
import io.helidon.websocket.{WsListener, WsSession}

import java.io.{ByteArrayOutputStream, PrintWriter}

class ServerWsListener(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]) extends WsListener:

  private val im = invokerMap.map:
    case (c4, i) =>
      (c4.toRawCoordinates, i)

  override def onMessage(session: WsSession, buffer: BufferData, last: Boolean): Unit =
    val receiverId     = buffer.readInt32()
    val corId          = buffer.readLong()
    val coordsLength   = buffer.readUnsignedInt32()
    val coordsByteData = new Array[Byte](coordsLength.toInt)
    buffer.read(coordsByteData)
    val coordsStr      = new String(coordsByteData, "UTF-8")
    val coordinates4   = Coordinates4.unapply(coordsStr)
    val dataLen        = buffer.readInt32()
    val data           = new Array[Byte](dataLen)
    buffer.read(data)
    val argLen         = buffer.readInt32()
    val arg            = new Array[Byte](argLen)
    buffer.read(arg)
    try
      val f        = im(coordinates4.toRawCoordinates)
      val response = f(ReceiverInput(data, arg))
      val buf      = BufferData.growing(response.length + 12)
      buf.writeInt32(receiverId)
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
        val buf  = BufferData.growing(data.length + 12)
        buf.writeInt32(receiverId)
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
