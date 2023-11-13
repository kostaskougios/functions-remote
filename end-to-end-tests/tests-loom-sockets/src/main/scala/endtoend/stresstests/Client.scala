package endtoend.stresstests

import endtoend.tests.SimpleFunctionsCallerFactory
import functions.fibers.FiberExecutor
import functions.sockets.SocketTransport

import java.util.concurrent.atomic.AtomicInteger

@main def stressTestClient(): Unit =
  val transport = SocketTransport("localhost", 7201, poolSz = 64)
  val caller    = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)
  FiberExecutor.withFiberExecutor: fiberExecutor =>
    val requests = new AtomicInteger(0)
    val fibers   = for i <- 1 to 1000 yield fiberExecutor:
      for j <- 1 to 1000 do
        try
          val r = caller.add(j, i + 1)
          if r != j + i + 1 then throw new IllegalStateException(s"Server responded with wrong result at the ${j}th request.")
          requests.incrementAndGet()
        catch case t: Throwable => t.printStackTrace()

    // wait for them
    while !fibers.forall(_.isReady) do
      Thread.sleep(1000)
      val pool = transport.pool
      println(
        s"At the ${requests.get()}th request, activeThreads: ${ThreadCounter.countThreads()}, open sockets: ${pool.activeSockets}, idle sockets: ${pool.idleSockets.size}"
      )
