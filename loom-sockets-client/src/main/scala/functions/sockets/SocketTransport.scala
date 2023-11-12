package functions.sockets

import functions.model.TransportInput

import java.io.*
import java.net.*

class SocketTransport(host: String, port: Int, bufferSize: Int = 16384):
  def transportFunction(trIn: TransportInput): Array[Byte] =
    val s   = new Socket(InetAddress.getByName(host), port)
    val out = s.getOutputStream
    val in  = s.getInputStream
    try
      val coordData = trIn.coordinates4.toRawCoordinates.getBytes("UTF-8")
      out.write(coordData.length)
      out.write(coordData)
      out.write(trIn.data.length)
      out.write(trIn.data)
      out.flush()
      inputStreamToByteArray(in)
    finally
      out.close()
      in.close()
      s.close()

  private def inputStreamToByteArray(inputStream: InputStream): Array[Byte] =
    val buffer = new ByteArrayOutputStream(bufferSize)
    var nRead  = 0
    val data   = new Array[Byte](bufferSize)

    def next() =
      nRead = inputStream.read(data, 0, data.length)
      nRead

    while (next() != -1)
      buffer.write(data, 0, nRead);

    buffer.flush();
    buffer.toByteArray
