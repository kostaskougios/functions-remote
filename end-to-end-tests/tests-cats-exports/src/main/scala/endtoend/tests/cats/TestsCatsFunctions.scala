package endtoend.tests.cats

import cats.effect.kernel.Async
import endtoend.tests.cats.model.Return1

/** mark as exported: //> exported
  * @tparam F
  *   The IO monad
  */
trait TestsCatsFunctions[F[_]: Async]:
  def catsAdd(a: Int, b: Int): F[Int]
  def catsUnitResult(a: Int, b: Int): F[Unit]
  def catsAddR(a: Int, b: Int): F[Return1]
  def catsAddLR(a: Int, b: Int): F[List[Return1]]
  def catsDivide(a: Int, b: Int): F[Either[Int, String]]
  def alwaysFails(a: Int): F[String]
  def alwaysFailsBeforeCreatingF(a: Int): F[String]

  /** Use GET method for this: //> HTTP-GET
    */
  def catsAddParamsEmptySecond(a: Int, l: Long, s: String)(): F[Int]

  /** Use POST method for this: //> HTTP-POST
    */
  def catsAddParams(a: Int, l: Long, s: String)(b: Int): F[Int]
