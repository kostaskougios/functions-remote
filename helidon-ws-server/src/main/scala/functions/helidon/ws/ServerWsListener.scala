package functions.helidon.ws

import functions.fibers.FiberExecutor
import functions.helidon.ws.transport.ClientServerWsListener
import functions.model.InvokerMap

object ServerWsListener:
  def apply(invokerMap: InvokerMap, fiberExecutor: FiberExecutor, sendTimeoutInMillis: Long = 4000) =
    new ClientServerWsListener(new InOutMessageProtocol(invokerMap), fiberExecutor, sendTimeoutInMillis)
