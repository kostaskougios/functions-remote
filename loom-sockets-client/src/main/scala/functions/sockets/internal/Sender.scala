package functions.sockets.internal

import functions.sockets.internal.errors.RequestFailedException

import java.util.concurrent.CountDownLatch

case class Sender(correlationId: Int, data: Array[Byte]):
  @volatile private var responseData  = Array.emptyByteArray
  @volatile private var responseError = Option.empty[Throwable]
  private val latch                   = new CountDownLatch(1)

  def reply(data: Array[Byte]): Unit =
    responseData = data
    latch.countDown()

  def fail(t: Throwable): Unit =
    responseError = Some(t)
    latch.countDown()

  def response(): Array[Byte] =
    latch.await()
    responseError.foreach(t => throw new RequestFailedException(t))
    responseData
