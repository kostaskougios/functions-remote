package functions.helidon.requests

trait ParamConverter[A]:
  def apply(s: String): A

object ParamConverter:
  given ParamConverter[Int]     = _.toInt
  given ParamConverter[Long]    = _.toLong
  given ParamConverter[Boolean] = _.toBoolean
  given ParamConverter[String]  = identity(_)
