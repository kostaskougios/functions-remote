package functions.sockets

import functions.fibers.FiberExecutor
import functions.lib.logging.Logger
import functions.model.{Coordinates4, ReceiverInput}
import functions.sockets.internal.{RequestProcessor, RequestProtocol}

import java.io.{DataInputStream, DataOutputStream}
import java.net.{InetAddress, InetSocketAddress, ServerSocket, Socket}
import java.util
import java.util.concurrent.atomic.AtomicBoolean

class FiberSocketServer private (serverSocket: ServerSocket, executor: FiberExecutor, logger: Logger, perStreamQueueSz: Int):
  private val stopServer = new AtomicBoolean(false)
  private val protocol   = new RequestProtocol
  private val stats      = new ServerStats

  private def acceptOneSocketConnection(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    val clientSocket = serverSocket.accept()
    executor.submit(processRequest(clientSocket, invokerMap))

  def listen(invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): StartedFiberSocketServer =
    val serverFiber = executor.submit:
      while (!stopServer.get())
        runAndLogIgnoreError(acceptOneSocketConnection(invokerMap))
    new StartedFiberSocketServer(serverFiber, serverSocket, stopServer, stats)

  private def runAndLogIgnoreError(f: => Unit) = try f
  catch case t: Throwable => if !stopServer.get() then logger.error(t)

  private def processRequest(s: Socket, invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]]): Unit =
    stats.activeConnectionsCounter.incrementAndGet()

    val in  = new DataInputStream(s.getInputStream)
    val out = new DataOutputStream(s.getOutputStream)
    val rp  = new RequestProcessor(executor, in, out, invokerMap, stats, logger, perStreamQueueSz, protocol)

    try rp.serve()
    finally
      stats.activeConnectionsCounter.decrementAndGet()
      runAndLogIgnoreError(s.close())

object FiberSocketServer:
  def startServer(
      listenPort: Int,
      invokerMap: Map[Coordinates4, ReceiverInput => Array[Byte]],
      executor: FiberExecutor,
      backlog: Int = 64,
      logger: Logger = Logger.Console,
      receiveBufferSize: Int = 32768,
      // increase this if the server code has a lot of blocking calls and the client is sending requests very quickly
      perStreamQueueSz: Int = 256
  ): StartedFiberSocketServer =
    val server                   = new ServerSocket()
    server.setReceiveBufferSize(receiveBufferSize)
    server.bind(new InetSocketAddress(null.asInstanceOf[InetAddress], listenPort), backlog)
    val s                        = new FiberSocketServer(server, executor, logger, perStreamQueueSz)
    val startedFiberSocketServer = s.listen(invokerMap)
    var i                        = 8192
    while (!startedFiberSocketServer.isRunning && i > 0)
      i = i - 1
      Thread.`yield`()
    if i == 0 then throw new IllegalStateException("Could not start accepting requests.")
    startedFiberSocketServer
