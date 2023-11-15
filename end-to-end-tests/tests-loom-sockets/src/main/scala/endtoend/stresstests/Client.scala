package endtoend.stresstests

import endtoend.tests.model.{CombinedReturn, Param1, Param2, Return1}
import endtoend.tests.{NestedTypeParamsFunctionsCallerFactory, SimpleFunctionsCallerFactory}
import functions.fibers.FiberExecutor
import functions.sockets.SocketTransport

import java.util.concurrent.atomic.AtomicInteger

@main def stressTestClient(): Unit =
  val transport = SocketTransport("localhost", 7201, poolSz = 512)
  val caller    = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)
  val ntCaller  = NestedTypeParamsFunctionsCallerFactory.newAvroNestedTypeParamsFunctions(transport.transportFunction)
  FiberExecutor.withFiberExecutor: fiberExecutor =>
    val requests = new AtomicInteger(0)
    val fibers   =
      for i <- 1 to 1000
      yield fiberExecutor.submit:
        for j <- 1 to 1000 do
          try
            val r = caller.add(j, i + 1)
            if r != j + i + 1 then throw new IllegalStateException(s"Server responded with wrong result at the ${j}th request.")
//            val ntR = ntCaller.seqOfP1P2(Seq.fill(20)(Param1(i + j)), Seq.fill(10)(Param2(i + j + 1.5f)))
//            if ntR != CombinedReturn(Seq.fill(20)(Return1(i + j)), Seq.fill(10)(Return1(i + j + 1))) then
//              throw new IllegalStateException(s"Server responded with wrong ntR result at the ${j}th request.")
            requests.addAndGet(1)
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
