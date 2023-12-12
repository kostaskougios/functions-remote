package functions.fibers

import java.util.concurrent.Future
import scala.concurrent.duration.TimeUnit

case class Fiber[A](javaFuture: Future[A]):
  def isReady: Boolean                          = javaFuture.isDone
  def interrupt(): Unit                         = javaFuture.cancel(true)
  def get(): A                                  = javaFuture.get()
  def get(timeout: Long, timeUnit: TimeUnit): A = javaFuture.get(timeout, timeUnit)
  def await(): Either[A, Throwable]             =
    try Left(get())
    catch case t: Throwable => Right(t)
  def state: Future.State                       = javaFuture.state()
  def isRunning: Boolean                        = state == Future.State.RUNNING
  def isFailed: Boolean                         = state == Future.State.FAILED
  def isSuccess: Boolean                        = state == Future.State.SUCCESS
  def isCancelled: Boolean                      = state == Future.State.CANCELLED
