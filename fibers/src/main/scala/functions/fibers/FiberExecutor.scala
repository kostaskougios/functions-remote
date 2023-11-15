package functions.fibers

import java.util.concurrent.{Callable, ExecutorService, Executors, Future}

class FiberExecutor private (executorService: ExecutorService):
  def submit[R](f: => R): Fiber[R] =
    val c: Callable[R] = () => f
    Fiber(executorService.submit(c))

  def shutdown(): Unit = executorService.shutdown()

case class Fiber[A](javaFuture: Future[A]):
  def result: A         = javaFuture.get()
  def isReady: Boolean  = javaFuture.isDone
  def interrupt(): Unit = javaFuture.cancel(true)
  def get(): A          = javaFuture.get()

object FiberExecutor:
  def apply(): FiberExecutor =
    val executor = Executors.newVirtualThreadPerTaskExecutor
    new FiberExecutor(executor)

  def withFiberExecutor[R](f: FiberExecutor => R): R =
    val executor = Executors.newVirtualThreadPerTaskExecutor
    try
      val fe = new FiberExecutor(executor)
      f(fe)
    finally executor.shutdown()
