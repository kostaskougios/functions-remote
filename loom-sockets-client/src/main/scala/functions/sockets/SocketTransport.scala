package functions.sockets

import functions.model.TransportInput

import java.io.*

class SocketTransport(val pool: SocketPool):

  def transportFunction(trIn: TransportInput): Array[Byte] =
    val out       = new ByteArrayOutputStream(8192)
    val coordData = trIn.coordinates4.toRawCoordinates.getBytes("UTF-8")
    out.write(coordData.length)
    out.write(coordData)
    out.write(trIn.data.length)
    out.write(trIn.data)
    out.flush()
    pool.send(out.toByteArray)

  private def inputStreamToByteArray(inputStream: InputStream): Array[Byte] =
    val sz   = inputStream.read()
    val data = new Array[Byte](sz)
    inputStream.read(data)
    data

object SocketTransport:
  def apply(host: String, port: Int, poolSz: Int = 32): SocketTransport =
    val pool = SocketPool(host, port, poolSz)
    new SocketTransport(pool)
