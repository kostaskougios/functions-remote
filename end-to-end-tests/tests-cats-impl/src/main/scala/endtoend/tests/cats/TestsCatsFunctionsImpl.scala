package endtoend.tests.cats

import cats.effect.kernel.Async

class TestsCatsFunctionsImpl[F[_]: Async] extends TestsCatsFunctions[F]:
  private val A = Async[F]

  override def noCatsAdd(a: Int, b: Int): Int = a + b

  override def catsAdd(a: Int, b: Int): F[Int] = A.pure(a + b)
