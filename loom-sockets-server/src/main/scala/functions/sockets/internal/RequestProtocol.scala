package functions.sockets.internal

import functions.model.Coordinates4
import functions.sockets.CommonCodes

import java.io.{DataInputStream, DataOutputStream}

class RequestProtocol:
  def writer(req: InvocationOutcome, out: DataOutputStream): Unit =
    req match
      case InvocationSuccess(_, outData) =>
        out.write(CommonCodes.ResponseSuccess)
        out.writeInt(outData.length)
        out.write(outData)
      case f: InvocationFailure          =>
        out.write(CommonCodes.ResponseError)
        val errorData = f.exceptionToByteArray
        out.writeInt(errorData.length)
        out.write(errorData)

  def reader(in: DataInputStream): (Coordinates4, Array[Byte]) =
    val coordsSz    = in.readInt()
    val coordsRaw   = new String(in.readNBytes(coordsSz), "UTF-8")
    val coordinates = Coordinates4(coordsRaw)
    val inData      = inputStreamToByteArray(in)
    (coordinates, inData)

  private def inputStreamToByteArray(in: DataInputStream): Array[Byte] =
    val dataSz = in.readInt()
    val data   = new Array[Byte](dataSz)
    in.read(data)
    data
