package functions.sockets

import java.io.{ByteArrayOutputStream, InputStream}
import java.net.{ServerSocket, Socket}
import java.util
import java.util.concurrent.{Executors, Future}
import scala.jdk.CollectionConverters.*

type RequestProcessor = Array[Byte] => Array[Byte]

// https://wiki.openjdk.org/display/loom/Getting+started
class FiberSocketServer(listenPort: Int, bufferSize: Int = 16384):
  private val server   = new ServerSocket(listenPort)
  private val executor = Executors.newVirtualThreadPerTaskExecutor

  def shutdown(): Unit             = executor.shutdown()
  def shutdownNow(): Seq[Runnable] = executor.shutdownNow().asScala.toList

  def acceptOne[R](processor: RequestProcessor): Future[_] =
    val clientSocket = server.accept()
    executor.submit(runnable(clientSocket, processor))

  protected def logError(throwable: Throwable) = throwable.printStackTrace()

  private def runnable(clientSocket: Socket, processor: RequestProcessor): Runnable =
    () =>
      val in  = clientSocket.getInputStream
      val out = clientSocket.getOutputStream
      try
        val inData  = inputStreamToByteArray(in)
        val outData = processor(inData)
        out.write(outData)
        out.flush()
      catch case t: Throwable => logError(t)
      finally
        in.close()
        out.close()
        clientSocket.close()

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
