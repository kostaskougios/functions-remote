package functions.sockets

import functions.model.TransportInput

import java.io.*

class SocketTransport(pool: SocketPool):

  def transportFunction(trIn: TransportInput): Array[Byte] =
    val out       = new ByteArrayOutputStream(8192)
    val dos       = new DataOutputStream(out)
    val coordData = trIn.coordinates4.toRawCoordinates.getBytes("UTF-8")
    dos.writeInt(coordData.length)
    dos.write(coordData)
    dos.writeInt(trIn.data.length)
    dos.write(trIn.data)
    dos.flush()
    pool.send(out.toByteArray)
