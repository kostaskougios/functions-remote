package functions.fibers

import java.util.concurrent.{Callable, CountDownLatch, ExecutorService, Executors}
import scala.util.Using.Releasable

// https://wiki.openjdk.org/display/loom/Getting+started
// https://openjdk.org/jeps/444
class FiberExecutor private (executorService: ExecutorService):
  def submit[R](f: => R): Fiber[R] =
    val c: Callable[R] = () => f
    Fiber(executorService.submit(c))

  def two[A, B](fiber1: TwoFibers[A, B] => A, fiber2: TwoFibers[A, B] => B): TwoFibers[A, B] =
    val l                              = new CountDownLatch(3)
    @volatile var two: TwoFibers[A, B] = null

    val f1 = submit {
      l.countDown()
      l.await()
      fiber1(two)
    }

    val f2 = submit {
      l.countDown()
      l.await()
      fiber2(two)
    }
    two = TwoFibers(f1, f2)
    l.countDown()
    two

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
