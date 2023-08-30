package mustache.integration

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class MustacheTemplateTest extends AnyFunSuiteLike:
  test("renders vars") {
    case class Vals(name: String)
    MustacheTemplate("Hello {{name}}").apply(Vals("world")) should be("Hello world")
  }
