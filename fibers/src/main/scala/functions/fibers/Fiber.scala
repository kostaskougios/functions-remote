package functions.fibers

import java.util.concurrent.Future

case class Fiber[A](javaFuture: Future[A]):
  def isReady: Boolean              = javaFuture.isDone
  def interrupt(): Unit             = javaFuture.cancel(true)
  def get(): A                      = javaFuture.get()
  def await(): Either[A, Throwable] =
    try Left(get())
    catch case t: Throwable => Right(t)
  def state: Future.State           = javaFuture.state()
  def isRunning: Boolean            = state == Future.State.RUNNING
  def isFailed: Boolean             = state == Future.State.FAILED
  def isSuccess: Boolean            = state == Future.State.SUCCESS
  def isCancelled: Boolean          = state == Future.State.CANCELLED
