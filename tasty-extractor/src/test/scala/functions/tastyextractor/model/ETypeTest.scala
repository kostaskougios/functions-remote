package functions.tastyextractor.model

import functions.tastyextractor.model.EBuilders.eType
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class ETypeTest extends AnyFunSuiteLike:
  test("breakdown with no type args") {
    eType("String", "scala.String").breakdown should be(ETypeBreakdown(List("scala"), "String", None))
  }

  test("breakdown with type args") {
    eType("List", "scala.List[Int]").breakdown should be(ETypeBreakdown(List("scala"), "List", Some("Int")))
  }

  test("codeNoTypeArgs") {
    ETypeBreakdown(List("scala"), "List", Some("Int")).codeNoTypeArgs should be("scala.List")
  }

  test("codeNoPackages positive") {
    ETypeBreakdown(List("scala"), "List", Some("Int")).codeNoPackages should be("List[Int]")
  }
