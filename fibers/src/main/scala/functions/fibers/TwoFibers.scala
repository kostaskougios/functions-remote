package functions.fibers

case class TwoFibers[A, B](fiber1: Fiber[A], fiber2: Fiber[B]):
  def get(): (A, B) =
    (fiber1.get(), fiber2.get())

  def await(): (Either[A, Throwable], Either[B, Throwable]) =
    val r1 = fiber1.await()
    val r2 = fiber2.await()
    (r1, r2)
