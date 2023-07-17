package org.simplified.templates

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class ScalaFileTemplatesTest extends AnyFunSuiteLike:
  val templates = ScalaFileTemplates()
  test("replaces vals") {
    case class Vals(x: String, y: String)
    val vals = Vals("a", "b")
    templates(
      """
         |val `x`=5
         |val `y`=6
         |""".stripMargin.trim,
      vals
    ) should be("""
        |val a=5
        |val b=6
        |""".stripMargin.trim)
  }
