package functions.sockets

import functions.model.TransportInput

import java.io.*

class SocketTransport(pool: SocketPool):

  def transportFunction(trIn: TransportInput): Array[Byte] =
    pool.withSocket: s =>
      val out = s.getOutputStream
      val in  = s.getInputStream

      val coordData = trIn.coordinates4.toRawCoordinates.getBytes("UTF-8")
      out.write(coordData.length)
      out.write(coordData)
      out.write(trIn.data.length)
      out.write(trIn.data)
      out.flush()
      inputStreamToByteArray(in)

  private def inputStreamToByteArray(inputStream: InputStream): Array[Byte] =
    val sz   = inputStream.read()
    val data = new Array[Byte](sz)
    inputStream.read(data)
    data
