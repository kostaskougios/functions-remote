package functions.fibers

import java.util.concurrent.{Callable, ExecutorService, Executors, Future}

class FiberExecutor private (executorService: ExecutorService):
  def run[R](f: => R): Fiber[R] =
    val c: Callable[R] = () => f
    Fiber(executorService.submit(c))

  def shutdown(): Unit = executorService.shutdown()

case class Fiber[A](javaFuture: Future[A]):
  def result: A        = javaFuture.get()
  def isReady: Boolean = javaFuture.isDone

object FiberExecutor:
  def withFiberExecutor[R](f: FiberExecutor => R): R =
    val executor = Executors.newVirtualThreadPerTaskExecutor
    try
      val fe = new FiberExecutor(executor)
      f(fe)
    finally executor.shutdown()
