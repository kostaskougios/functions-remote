package functions.sockets

import functions.fibers.{Fiber, FiberExecutor}
import functions.lib.logging.Logger
import functions.model.{Coordinates4, ReceiverInput}
import functions.sockets.internal.RequestProcessor

import java.io.{DataInputStream, DataOutputStream}
import java.net.{InetAddress, InetSocketAddress, ServerSocket, Socket}
import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}
import scala.util.Using.Releasable

class FiberSocketServer private (serverSocket: ServerSocket, executor: FiberExecutor, logger: Logger):
  private val stopServer               = new AtomicBoolean(false)
  private val totalRequestCounter      = new AtomicLong(0)
  private val servingCounter           = new AtomicInteger(0)
  private val activeConnectionsCounter = new AtomicInteger(0)
  def totalRequestCount: Long          = totalRequestCounter.get()
  def servingCount: Long               = servingCounter.get()
  def activeConnectionsCount: Long     = activeConnectionsCounter.get()

  private def interruptServerThread(): Unit =
    stopServer.set(true)
    if serverFiber != null then serverFiber.interrupt()

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
  catch case t: Throwable => if !stopServer.get() then logger.error(t)

  private def processRequest(s: Socket, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    activeConnectionsCounter.incrementAndGet()

    val in  = new DataInputStream(s.getInputStream)
    val out = new DataOutputStream(s.getOutputStream)
    val rp  = new RequestProcessor(executor, in, out, invokerMap, totalRequestCounter, servingCounter, logger)

    try rp.serve()
    finally
      activeConnectionsCounter.decrementAndGet()
      runAndLogIgnoreError(s.close())

  def shutdown(): Unit =
    interruptServerThread()
    serverFiber.await()
    runAndLogIgnoreError(serverSocket.close())

object FiberSocketServer:
  def startServer(
      listenPort: Int,
      invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]],
      executor: FiberExecutor,
      backlog: Int = 64,
      logger: Logger = Logger.Console
  ): FiberSocketServer =
    val server      = new ServerSocket()
    server.setReceiveBufferSize(32768)
    server.bind(new InetSocketAddress(null.asInstanceOf[InetAddress], listenPort), backlog)
    val s           = new FiberSocketServer(server, executor, logger)
    val serverFiber = s.start(invokerMap)
    var i           = 8192
    while (!serverFiber.isRunning && i > 0)
      i = i - 1
      Thread.`yield`()
    if i == 0 then throw new IllegalStateException("Could not start accepting requests.")
    s

  given Releasable[FiberSocketServer] = server => server.shutdown()
