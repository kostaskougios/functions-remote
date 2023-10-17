package endtoend.tests.cats

import cats.effect.kernel.Async
import cats.implicits.*
import endtoend.tests.cats.model.Return1

class TestsCatsFunctionsImpl[F[_]: Async] extends TestsCatsFunctions[F]:
  private val A = Async[F]

  override def noCatsAdd(a: Int, b: Int): Int = a + b

  override def catsAdd(a: Int, b: Int): F[Int] = A.pure(a + b)

  override def catsAddR(a: Int, b: Int): F[Return1] = A.pure(Return1(a + b))

  override def catsAddLR(a: Int, b: Int): F[List[Return1]] = catsAddR(a, b).map(r => List(r))
