package mustache.integration

import com.github.mustachejava.TemplateFunction
import com.github.mustachejava.util.DecoratedCollection
import mustache.integration.model.Many
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

import scala.jdk.CollectionConverters.*
import mustache.integration.model.CollectionConverters.given
import scala.language.implicitConversions

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

  test("DecoratedCollection") {
    case class Item(name: String)
    case class Vals(items: DecoratedCollection[Item])

    MustacheTemplate("{{#items}}{{value.name}}{{^last}},{{/last}}{{/items}}", "test-template").apply(
      Vals(Seq(Item("n1"), Item("n2")))
    ) should be("n1,n2")
  }
  test("functions") {
    case class Vals(name: String, f: TemplateFunction)
    def f(s: String) = s"value of s = [$s]"
    MustacheTemplate("{{#f}}hello{{/f}}", "test-template").apply(Vals("a-name", f)) should be("value of s = [hello]")
  }
