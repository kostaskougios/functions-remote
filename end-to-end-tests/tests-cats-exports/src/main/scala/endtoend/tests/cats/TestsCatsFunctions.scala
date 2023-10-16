package endtoend.tests.cats

import cats.effect.kernel.Async

/** mark as exported: //> exported
  * @tparam F
  *   The IO monad
  */
trait TestsCatsFunctions[F[_]: Async]:
  def noCatsAdd(a: Int, b: Int): Int
  def catsAdd(a: Int, b: Int): F[Int]
