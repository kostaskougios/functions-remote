package ls

import ls.model.{LsFile, LsOptions, LsResult}
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class LsFunctionsCallerTest extends AnyFunSuiteLike {
  test("chains calls correctly"):
    val byteData = Array(1, 2).map(_.toByte)
    val expected = LsResult(Seq(LsFile("test")))

    def toByteArray(p: LsFunctionsMethods)           =
      p should be(LsFunctionsMethods.Ls("/home", LsOptions()))
      byteData
    def callFunction(method: String, a: Array[Byte]) =
      method should be(LsFunctionsMethods.Methods.Ls)
      a should be(byteData)
      expected

    val caller = new LsFunctionsCaller(toByteArray, _ => Array.emptyByteArray, callFunction)
    caller.ls("/home", LsOptions()) should be(expected)
}
