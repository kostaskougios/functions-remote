package endtoend.stresstests

import endtoend.tests.SimpleFunctionsCallerFactory
import functions.sockets.SocketTransport

@main def stressTestClient(): Unit =
  val transport = new SocketTransport("localhost", 7200)
  val caller    = SimpleFunctionsCallerFactory.newAvroSimpleFunctions(transport.transportFunction)
  for i <- 1 to 1_000_000 do
    val r = caller.add(i, i + 1)
    if r != i + i + 1 then throw new IllegalStateException(s"Server responded with wrong result at the ${i}th request.")
    if i % 10000 == 0 then println(s"At the ${i}th request")
