package functions.sockets

import functions.fibers.Fiber

import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Using.Releasable

class StartedFiberSocketServer(serverFiber: Fiber[Unit], serverSocket: ServerSocket, stopServer: AtomicBoolean, val stats: ServerStats):
  private def interruptServerThread(): Unit =
    stopServer.set(true)
    if serverFiber != null then serverFiber.interrupt()

  def isRunning: Boolean = serverFiber.isRunning

  def shutdown(): Unit =
    interruptServerThread()
    serverFiber.await()
    serverSocket.close()

object StartedFiberSocketServer:
  given Releasable[StartedFiberSocketServer] = server => server.shutdown()
