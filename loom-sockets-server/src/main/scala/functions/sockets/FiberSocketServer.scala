package functions.sockets

import functions.fibers.{Fiber, FiberExecutor}
import functions.model.{Coordinates4, ReceiverInput}

import java.io.{DataInputStream, DataOutputStream, EOFException, InputStream}
import java.net.{ServerSocket, Socket}
import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}

// https://wiki.openjdk.org/display/loom/Getting+started
// https://openjdk.org/jeps/444

class FiberSocketServer private (serverSocket: ServerSocket, executor: FiberExecutor):
  def shutdown(): Unit =
    interruptServerThread()
    runAndLogIgnoreError(serverSocket.close())

  private val stopServer               = new AtomicBoolean(false)
  private val totalRequestCounter      = new AtomicLong(0)
  private val servingCounter           = new AtomicInteger(0)
  private val activeConnectionsCounter = new AtomicInteger(0)
  def totalRequestCount: Long          = totalRequestCounter.get()
  def servingCount: Long               = servingCounter.get()
  def activeConnectionsCount: Long     = activeConnectionsCounter.get()

  private def interruptServerThread(): Unit =
    stopServer.set(true)
    Thread.`yield`()
    if serverFiber != null then serverFiber.interrupt()
    serverFiber = null

  private def acceptOneSocketConnection(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    val clientSocket = serverSocket.accept()
    executor.submit(processRequest(clientSocket, invokerMap))

  @volatile private var serverFiber: Fiber[Unit] = null

  private def start(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Fiber[Unit] =
    def listen(): Unit =
      while (!stopServer.get())
        runAndLogIgnoreError(acceptOneSocketConnection(invokerMap))
    serverFiber = executor.submit(listen())
    serverFiber

  private def runAndLogIgnoreError(f: => Unit) = try f
  catch case t: Throwable => logError(t)

  protected def logError(throwable: Throwable): Unit =
    if !stopServer.get() then throwable.printStackTrace()

  private def processRequest(s: Socket, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    try
      activeConnectionsCounter.incrementAndGet()
      val in  = new DataInputStream(s.getInputStream)
      val out = new DataOutputStream(s.getOutputStream)

      def serveOne(): Boolean =
        in.readInt() match
          case 0             =>
            throw new IllegalStateException("Incorrect data on the socket (maybe from the client)")
          case correlationId =>
            try
              totalRequestCounter.incrementAndGet()
              servingCounter.incrementAndGet()
              val coordsSz    = in.readInt()
              val coordsRaw   = new String(in.readNBytes(coordsSz), "UTF-8")
              val coordinates = Coordinates4(coordsRaw)
              val inData      = inputStreamToByteArray(in)
              val outData     = invokerMap(coordinates)(ReceiverInput(inData))
              out.writeInt(correlationId)
              out.writeInt(outData.length)
              out.write(outData)
              out.flush()
              true
            finally servingCounter.decrementAndGet()

      while (s.isConnected && serveOne()) {}
    catch
      case _: EOFException => // ignore
      case t: Throwable    => logError(t)
    finally
      activeConnectionsCounter.decrementAndGet()
      s.close()

  private def inputStreamToByteArray(in: DataInputStream): Array[Byte] =
    val dataSz = in.readInt()
    val data   = new Array[Byte](dataSz)
    in.read(data)
    data

object FiberSocketServer:
  def withServer[R](listenPort: Int, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]], backlog: Int = 64)(f: FiberSocketServer => R) =
    val server = new ServerSocket(listenPort, backlog)
    FiberExecutor.withFiberExecutor: executor =>
      val s = new FiberSocketServer(server, executor)
      try
        val serverFiber = s.start(invokerMap)
        var i           = 8192
        while (!serverFiber.isRunning && i > 0)
          i = i - 1
          Thread.`yield`()
        if i == 0 then throw new IllegalStateException("Could not start accepting requests.")
        f(s)
      finally s.shutdown()
