package functions.fibers

import java.util.concurrent.{Callable, ExecutorService, Executors, Future}
import scala.util.Using.Releasable

// https://wiki.openjdk.org/display/loom/Getting+started
// https://openjdk.org/jeps/444
class FiberExecutor private (executorService: ExecutorService):
  def submit[R](f: => R): Fiber[R] =
    val c: Callable[R] = () => f
    Fiber(executorService.submit(c))

  def shutdown(): Unit = executorService.shutdown()

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

object FiberExecutor:
  def apply(): FiberExecutor =
    val executor = Executors.newVirtualThreadPerTaskExecutor
    new FiberExecutor(executor)

  given Releasable[FiberExecutor] = resource => resource.shutdown()

  def withFiberExecutor[R](f: FiberExecutor => R): R =
    val executor = Executors.newVirtualThreadPerTaskExecutor
    try
      val fe = new FiberExecutor(executor)
      f(fe)
    finally executor.shutdown()
