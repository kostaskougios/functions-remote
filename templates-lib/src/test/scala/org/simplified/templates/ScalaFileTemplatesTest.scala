package org.simplified.templates

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class ScalaFileTemplatesTest extends AnyFunSuiteLike:
  val templates = ScalaFileTemplates()
  test("replaces vals") {
    case class Vals(x: String, y: String)
    templates(
      """
        |val `x`=5
        |val `y`=6
        |""".stripMargin.trim,
      Vals("a", "b")
    ) should be("""
        |val a=5
        |val b=6
        |""".stripMargin.trim)
  }

  test("// = for") {
    case class LoopVals(x: String, y: Int)
    case class Vals(loop: Seq[LoopVals])
    templates(
      s"""
         |// = foreach loop
         |val `x`=`y`
         |// = end loop
         |""".stripMargin.trim,
      Vals(Seq(LoopVals("a", 1), LoopVals("b", 2)))
    ) should be("""
        |val a=1
        |val b=2
        |""".stripMargin.trim)
  }
