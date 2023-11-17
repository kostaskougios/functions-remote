package functions.fibers

import java.util.concurrent.{Callable, ExecutorService, Executors}
import scala.util.Using.Releasable

// https://wiki.openjdk.org/display/loom/Getting+started
// https://openjdk.org/jeps/444
class FiberExecutor private (executorService: ExecutorService):
  def submit[R](f: => R): Fiber[R] =
    val c: Callable[R] = () => f
    Fiber(executorService.submit(c))

  def shutdown(): Unit = executorService.shutdown()

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
