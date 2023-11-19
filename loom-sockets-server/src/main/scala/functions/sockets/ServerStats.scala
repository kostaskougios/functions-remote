package functions.sockets

import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}

class ServerStats:
  private[sockets] val totalRequestCounter      = new AtomicLong(0)
  private[sockets] val servingCounter           = new AtomicInteger(0)
  private[sockets] val activeConnectionsCounter = new AtomicInteger(0)

  def totalRequestCount: Long = totalRequestCounter.get()

  def servingCount: Long = servingCounter.get()

  def activeConnectionsCount: Long = activeConnectionsCounter.get()
