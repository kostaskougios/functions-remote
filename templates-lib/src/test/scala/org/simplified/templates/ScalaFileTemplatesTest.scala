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

  test("// = foreach") {
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

  test("// = foreach processes code abefore and fter the foreach") {
    case class LoopVals(x: String, y: Int)
    case class Vals(loop: Seq[LoopVals])
    templates(
      s"""
         |beforeForEachCode
         |// = foreach loop
         |val `x`=`y`
         |// = end loop
         |afterForEachCode
         |""".stripMargin.trim,
      Vals(Seq(LoopVals("a", 1), LoopVals("b", 2)))
    ) should be("""
        |beforeForEachCode
        |val a=1
        |val b=2
        |afterForEachCode
        |""".stripMargin.trim)
  }

  test("// = foreach vals precedence") {
    case class LoopVals(x: String)
    case class Vals(x: String, loop: Seq[LoopVals])
    templates(
      s"""
         |val `x`=1
         |// = foreach loop
         |val `x`=2
         |// = end loop
         |val `x`=3
         |""".stripMargin.trim,
      Vals("a", Seq(LoopVals("fa"), LoopVals("fb")))
    ) should be("""
        |val a=1
        |val fa=2
        |val fb=2
        |val a=3
        |""".stripMargin.trim)
  }
