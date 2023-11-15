package functions.sockets

import functions.fibers.{Fiber, FiberExecutor}
import functions.model.{Coordinates4, ReceiverInput}

import java.io.InputStream
import java.net.{ServerSocket, Socket}
import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}

// https://wiki.openjdk.org/display/loom/Getting+started
// https://openjdk.org/jeps/444

class FiberSocketServer private (serverSocket: ServerSocket, executor: FiberExecutor):
  def shutdown(): Unit =
    interruptServerThread()
    runAndLogIgnoreError(serverSocket.close())

  @volatile private var serverThread: Thread = null
  private val stopServer                     = new AtomicBoolean(false)
  private val accepting                      = new AtomicBoolean(false)
  private val totalRequestCounter            = new AtomicLong(0)
  private val servingCounter                 = new AtomicInteger(0)
  private val activeConnectionsCounter       = new AtomicInteger(0)
  def isAccepting: Boolean                   = accepting.get()
  def totalRequestCount: Long                = totalRequestCounter.get()
  def servingCount: Long                     = servingCounter.get()
  def activeConnectionsCount: Long           = activeConnectionsCounter.get()

  private def interruptServerThread(): Unit =
    stopServer.set(true)
    Thread.`yield`()
    if serverThread != null then serverThread.interrupt()
    serverThread = null

  private def acceptOneSocketConnection(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    serverThread = Thread.currentThread()
    try
      accepting.set(true)
      val clientSocket = serverSocket.accept()
      executor.submit(processRequest(clientSocket, invokerMap))
    finally accepting.set(false)

  private def start(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Fiber[Unit] =
    def listen(): Unit =
      while (!stopServer.get())
        runAndLogIgnoreError(acceptOneSocketConnection(invokerMap))
    executor.submit(listen())

  private def runAndLogIgnoreError(f: => Unit) = try f
  catch case t: Throwable => logError(t)

  protected def logError(throwable: Throwable): Unit =
    if !stopServer.get() then throwable.printStackTrace()

  private def processRequest(s: Socket, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    try
      activeConnectionsCounter.incrementAndGet()
      val in  = s.getInputStream
      val out = s.getOutputStream

      def serveOne(): Boolean =
        in.read() match
          case -1            => false
          case correlationId =>
            try
              totalRequestCounter.incrementAndGet()
              servingCounter.incrementAndGet()
              val coordsSz    = in.read()
              val coordsRaw   = new String(in.readNBytes(coordsSz), "UTF-8")
              val coordinates = Coordinates4(coordsRaw)
              val inData      = inputStreamToByteArray(in)
              val outData     = invokerMap(coordinates)(ReceiverInput(inData))
              out.write(correlationId)
              out.write(outData.length)
              out.write(outData)
              out.flush()
              true
            finally servingCounter.decrementAndGet()

      while (s.isConnected && serveOne()) {}

    catch case t: Throwable => logError(t)
    finally
      activeConnectionsCounter.decrementAndGet()
      s.close()

  private def inputStreamToByteArray(inputStream: InputStream): Array[Byte] =
    val dataSz = inputStream.read()
    val data   = new Array[Byte](dataSz)
    inputStream.read(data)
    data

object FiberSocketServer:
  def withServer[R](listenPort: Int, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]], backlog: Int = 64)(f: FiberSocketServer => R) =
    val server = new ServerSocket(listenPort, backlog)
    FiberExecutor.withFiberExecutor: executor =>
      val s = new FiberSocketServer(server, executor)
      try
        s.start(invokerMap)
        var i = 8192
        while (!s.isAccepting && i > 0)
          i = i - 1
          Thread.`yield`()
        if i == 0 then throw new IllegalStateException("Could not start accepting requests.")
        f(s)
      finally s.shutdown()
