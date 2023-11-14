package endtoend.stresstests

import endtoend.tests.SimpleFunctionsCallerFactory
import functions.fibers.FiberExecutor
import functions.sockets.SocketTransport

import java.util.concurrent.atomic.AtomicInteger

@main def stressTestClient(): Unit =
  val transport = SocketTransport("localhost", 7201, poolSz = 128)
  val caller    = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)
  FiberExecutor.withFiberExecutor: fiberExecutor =>
    val requests = new AtomicInteger(0)
    val fibers   =
      for i <- 1 to 1000
      yield fiberExecutor:
        for j <- 1 to 1000 do
          try
            val r = caller.add(j, i + 1)
            if r != j + i + 1 then throw new IllegalStateException(s"Server responded with wrong result at the ${j}th request.")
            requests.incrementAndGet()
          catch case t: Throwable => t.printStackTrace()

    // wait for them
    while !fibers.forall(_.isReady) do
      val startingRequestsCount = requests.get()
      Thread.sleep(1000)
      val pool                  = transport.pool
      val requestsCount         = requests.get()
      val dRequests             = requestsCount - startingRequestsCount
      println(
        s"At the ${requestsCount}th request, $dRequests per second, activeThreads: ${ThreadCounter
            .countThreads()}, open sockets: ${pool.activeSockets}, idle sockets: ${pool.idleSockets.size}, created sockets: ${pool.createdSocketsCount}, closed sockets: ${pool.invalidatedSocketsCount}"
      )
