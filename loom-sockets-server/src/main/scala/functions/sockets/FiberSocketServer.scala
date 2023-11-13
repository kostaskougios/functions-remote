package functions.sockets

import functions.model.{Coordinates4, ReceiverInput}

import java.io.InputStream
import java.net.{ServerSocket, Socket}
import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}
import java.util.concurrent.{ExecutorService, Executors, Future}

// https://wiki.openjdk.org/display/loom/Getting+started
// https://openjdk.org/jeps/444

class FiberSocketServer private (server: ServerSocket, executor: ExecutorService):
  def shutdown(): Unit =
    interruptServerThread()
    executor.shutdown()
    server.close()

  @volatile private var serverThread: Thread = null
  private val stopServer                     = new AtomicBoolean(false)
  private val accepting                      = new AtomicBoolean(false)
  private val requestCounter                 = new AtomicLong(0)
  private val servingCounter                 = new AtomicInteger(0)
  def isAccepting: Boolean                   = accepting.get()
  def requestCount: Long                     = requestCounter.get()
  def servingCount: Long                     = servingCounter.get()

  private def interruptServerThread(): Unit =
    stopServer.set(true)
    Thread.`yield`()
    if serverThread != null then serverThread.interrupt()

  private def acceptOne(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    serverThread = Thread.currentThread()
    if !stopServer.get() then
      try
        accepting.set(true)
        val clientSocket = server.accept()
        executor.submit(runnable(clientSocket, invokerMap))
      finally accepting.set(false)

  private def start(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Future[_] =
    def listen: Runnable =
      () =>
        while (!stopServer.get())
          try acceptOne(invokerMap)
          catch case t: Throwable => logError(t)
    executor.submit(listen)

  protected def logError(throwable: Throwable): Unit =
    if !stopServer.get() then throwable.printStackTrace()

  private def runnable(clientSocket: Socket, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Runnable =
    () =>
      requestCounter.incrementAndGet()
      val in  = clientSocket.getInputStream
      val out = clientSocket.getOutputStream
      try
        servingCounter.incrementAndGet()
        val coordsSz    = in.read()
        val coordsRaw   = new String(in.readNBytes(coordsSz), "UTF-8")
        val coordinates = Coordinates4(coordsRaw)
        val inData      = inputStreamToByteArray(in)
        val outData     = invokerMap(coordinates)(ReceiverInput(inData))
        out.write(outData)
        out.flush()
      catch case t: Throwable => logError(t)
      finally
        servingCounter.decrementAndGet()
        in.close()
        out.close()
        clientSocket.close()

  private def inputStreamToByteArray(inputStream: InputStream): Array[Byte] =
    val dataSz = inputStream.read()
    val data   = new Array[Byte](dataSz)
    inputStream.read(data)
    data

object FiberSocketServer:
  def withServer[R](listenPort: Int, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]])(f: FiberSocketServer => R) =
    val server   = new ServerSocket(listenPort)
    val executor = Executors.newVirtualThreadPerTaskExecutor
    val s        = new FiberSocketServer(server, executor)
    try
      s.start(invokerMap)
      var i = 8192
      while (!s.isAccepting && i > 0)
        i = i - 1
        Thread.`yield`()
      if i == 0 then throw new IllegalStateException("Could not start accepting requests.")
      f(s)
    finally s.shutdown()
