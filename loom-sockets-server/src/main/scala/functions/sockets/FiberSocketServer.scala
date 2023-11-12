package functions.sockets

import functions.model.{Coordinates4, ReceiverInput}

import java.io.InputStream
import java.net.{ServerSocket, Socket}
import java.util
import java.util.concurrent.{Executors, Future}
import scala.jdk.CollectionConverters.*

// https://wiki.openjdk.org/display/loom/Getting+started
// https://openjdk.org/jeps/444

class FiberSocketServer(listenPort: Int):
  private val server   = new ServerSocket(listenPort)
  private val executor = Executors.newVirtualThreadPerTaskExecutor

  def shutdown(): Unit             = executor.shutdown()
  def shutdownNow(): Seq[Runnable] = executor.shutdownNow().asScala.toList

  def acceptOne(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Future[_] =
    val clientSocket = server.accept()
    executor.submit(runnable(clientSocket, invokerMap))

  protected def logError(throwable: Throwable) = throwable.printStackTrace()

  private def runnable(clientSocket: Socket, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Runnable =
    () =>
      val in  = clientSocket.getInputStream
      val out = clientSocket.getOutputStream
      try
        val coordsSz    = in.read()
        val coordsRaw   = new String(in.readNBytes(coordsSz), "UTF-8")
        val coordinates = Coordinates4(coordsRaw)
        val inData      = inputStreamToByteArray(in)
        val outData     = invokerMap(coordinates)(ReceiverInput(inData))
        out.write(outData)
        out.flush()
      catch case t: Throwable => logError(t)
      finally
        in.close()
        out.close()
        clientSocket.close()

  private def inputStreamToByteArray(inputStream: InputStream): Array[Byte] =
    val dataSz = inputStream.read()
    val data   = new Array[Byte](dataSz)
    inputStream.read(data)
    data
