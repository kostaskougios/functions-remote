package mustache.integration

import mustache.integration.model.Many
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class MustacheTemplateTest extends AnyFunSuiteLike:
  test("renders vars") {
    case class Vals(name: String)
    MustacheTemplate("Hello {{name}}", "test-template").apply(Vals("world")) should be("Hello world")
  }

  test("renders condition positive") {
    case class Vals(cond: Boolean)
    MustacheTemplate("{{#cond}}Hello{{/cond}}", "test-template").apply(Vals(true)) should be("Hello")
  }

  test("renders condition negative") {
    case class Vals(cond: Boolean)
    MustacheTemplate("{{#cond}}Hello{{/cond}}", "test-template").apply(Vals(false)) should be("")
  }

  test("renders collections") {
    case class Item(name: String)
    case class Vals(items: Many[Item])
    MustacheTemplate("{{#items}}name={{name}}\n{{/items}}", "test-template").apply(Vals(Many(Item("phone"), Item("watch")))) should be(
      "name=phone\nname=watch\n"
    )
  }
