package endtoend.tests.cats

import cats.effect.kernel.Async
import endtoend.tests.cats.model.Return1

/** mark as exported: //> exported
  * @tparam F
  *   The IO monad
  */
trait TestsCatsFunctions[F[_]: Async]:
  def noCatsAdd(a: Int, b: Int): Int
  def catsAdd(a: Int, b: Int): F[Int]
  def catsAddR(a: Int, b: Int): F[Return1]
  def catsAddLR(a: Int, b: Int): F[List[Return1]]
  def noCatsDivide(a: Int, b: Int): Either[Int, String]
  def catsDivide(a: Int, b: Int): F[Either[Int, String]]
