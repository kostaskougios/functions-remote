package org.simplified.templates

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*
import org.simplified.templates.model.{Param, Params}

class ScalaFileTemplateTest extends AnyFunSuiteLike:
  test("replaces vals") {
    case class Vals(x: String, y: String)
    ScalaFileTemplate(
      """
        |val `x`=5
        |val `y`=6
        |""".stripMargin.trim
    ).apply(
      Vals("a", "b")
    ) should be("""
        |val a=5
        |val b=6
        |""".stripMargin.trim)
  }

  test("// foreach") {
    case class LoopVals(x: String, y: Int)
    case class Vals(loop: Seq[LoopVals])
    ScalaFileTemplate(
      """
         |// foreach loop
         |val `x`=`y`
         |// end loop
         |""".stripMargin.trim
    ).apply(
      Vals(Seq(LoopVals("a", 1), LoopVals("b", 2)))
    ) should be("""
        |val a=1
        |val b=2
        |""".stripMargin.trim)
  }

  test("// foreach processes code abefore and fter the foreach") {
    case class LoopVals(x: String, y: Int)
    case class Vals(loop: Seq[LoopVals])
    ScalaFileTemplate(
      """
         |beforeForEachCode
         |// foreach loop
         |val `x`=`y`
         |// end loop
         |afterForEachCode
         |""".stripMargin.trim
    ).apply(
      Vals(Seq(LoopVals("a", 1), LoopVals("b", 2)))
    ) should be("""
        |beforeForEachCode
        |val a=1
        |val b=2
        |afterForEachCode
        |""".stripMargin.trim)
  }

  test("// foreach vals precedence") {
    case class LoopVals(x: String)
    case class Vals(x: String, loop: Seq[LoopVals])
    ScalaFileTemplate(
      """
         |val `x`=1
         |// foreach loop
         |val `x`=2
         |// end loop
         |val `x`=3
         |""".stripMargin.trim
    ).apply(
      Vals("a", Seq(LoopVals("fa"), LoopVals("fb")))
    ) should be("""
        |val a=1
        |val fa=2
        |val fb=2
        |val a=3
        |""".stripMargin.trim)
  }

  test("method params replacement") {
    case class Vals(params: Params)
    ScalaFileTemplate(
      "def f(`params`:Int):Int"
    ).apply(
      Vals(Params.of(Param("a", "Int"), Param("b", "Long")))
    ) should be("def f(a:Int, b:Long):Int")
  }

  test("method params replacement, custom user params") {
    case class Vals(params: Params)
    ScalaFileTemplate(
      "def f(`params`:Int, p:String):Int"
    ).apply(
      Vals(Params.of(Param("a", "Int"), Param("b", "Long")))
    ) should be("def f(a:Int, b:Long, p:String):Int")
  }
