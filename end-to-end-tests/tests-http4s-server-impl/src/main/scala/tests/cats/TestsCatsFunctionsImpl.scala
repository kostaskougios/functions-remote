package tests.cats

import cats.effect.kernel.Async
import cats.implicits.*
import endtoend.tests.cats.TestsCatsFunctions
import endtoend.tests.cats.model.Return1

class TestsCatsFunctionsImpl[F[_]: Async] extends TestsCatsFunctions[F]:
  private val A = Async[F]

  override def catsUnitResult(a: Int, b: Int): F[Unit]            = A.pure(())
  override def catsAdd(a: Int, b: Int): F[Int]                    = A.pure(a + b)
  override def catsAddR(a: Int, b: Int): F[Return1]               = A.pure(Return1(a + b))
  override def catsAddLR(a: Int, b: Int): F[List[Return1]]        = catsAddR(a, b).map(r => List(r))
  override def catsDivide(a: Int, b: Int): F[Either[Int, String]] = A.pure(if b == 0 then Right("Div by zero") else Left(a / b))
  override def alwaysFails(a: Int): F[String]                     = A.raiseError(new IllegalArgumentException(s"$a is invalid"))
  override def alwaysFailsBeforeCreatingF(a: Int): F[String]      = throw new IllegalArgumentException(s"$a is invalid")

  override def catsAddParams(a: Int, l: Long, s: String)(b: Int): F[Int]      = A.pure(a + b + l.toInt + s.toInt)
  override def catsAddParamsEmptySecond(a: Int, l: Long, s: String)(): F[Int] = A.pure(a + l.toInt + s.toInt)
