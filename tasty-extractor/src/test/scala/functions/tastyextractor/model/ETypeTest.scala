package functions.tastyextractor.model

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class ETypeTest extends AnyFunSuiteLike:
  test("breakdown with no type args") {
    EType.code("String", "scala.String").breakdown should be(ETypeBreakdown(List("scala"), "String", None))
  }

  test("breakdown with type args") {
    EType.code("List", "scala.List[Int]").breakdown should be(ETypeBreakdown(List("scala"), "List", Some("Int")))
  }

  test("isAlwaysImported positive") {
    EType.code("List", "scala.List[Int]").isAlwaysImported should be(true)
  }

  test("isAlwaysImported negative") {
    EType.code("List", "custom.List[Int]").isAlwaysImported should be(false)
    EType.code("List", "scala.some.List[Int]").isAlwaysImported should be(false)
  }

  test("codeNoTypeArgs") {
    ETypeBreakdown(List("scala"), "List", Some("Int")).codeNoTypeArgs should be("scala.List")
  }

  test("codeNoPackages positive") {
    ETypeBreakdown(List("scala"), "List", Some("Int")).codeNoPackages should be("List[Int]")
  }
