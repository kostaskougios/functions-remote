package mustache.integration

import com.github.mustachejava.TemplateFunction
import com.github.mustachejava.util.DecoratedCollection
import mustache.integration.model.{Many, Template}
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

import scala.jdk.CollectionConverters.*
import mustache.integration.model.CollectionConverters.given

import scala.language.implicitConversions

class MustacheTemplateTest extends AnyFunSuiteLike:

  test("renders vars") {
    case class Vals(name: String)
    MustacheTemplate.inlined("Hello {{name}}", "test-template").apply(Vals("world")) should be("Hello world")
  }

  test("renders condition positive") {
    case class Vals(cond: Boolean)
    MustacheTemplate.inlined("{{#cond}}Hello{{/cond}}", "test-template").apply(Vals(true)) should be("Hello")
  }

  test("renders condition negative") {
    case class Vals(cond: Boolean)
    MustacheTemplate.inlined("{{#cond}}Hello{{/cond}}", "test-template").apply(Vals(false)) should be("")
  }

  test("renders collections") {
    case class Item(name: String)
    case class Vals(items: Many[Item])
    MustacheTemplate.inlined("{{#items}}name={{name}}\n{{/items}}", "test-template").apply(Vals(Many(Item("phone"), Item("watch")))) should be(
      "name=phone\nname=watch\n"
    )
  }

  test("DecoratedCollection") {
    case class Item(name: String)
    case class Vals(items: DecoratedCollection[Item])

    MustacheTemplate
      .inlined("{{#items}}{{value.name}}{{^last}},{{/last}}{{/items}}", "test-template")
      .apply(
        Vals(Seq(Item("n1"), Item("n2")))
      ) should be("n1,n2")
  }

  test("functions") {
    case class Vals(name: String, f: TemplateFunction)
    val f: TemplateFunction = s => s"value of s = [$s]"
    MustacheTemplate.inlined("{{#f}}hello{{/f}}", "test-template").apply(Vals("a-name", f)) should be("value of s = [hello]")
  }

  test("template") {
    case class Vals(name: String, t: Template)
    val v = Vals("test", Template("test-template", "name={{name}}"))
    MustacheTemplate.inlined("{{{t}}}.", "test-template").apply(v) should be("name=test.")
  }

  test("using template") {
    case class Vals(name: String)
    val v = Vals("test")
    val t = Template("tt", "name={{name}}")
    MustacheTemplate.inlined("{{{tt}}}.", "test-template", Seq(t)).apply(v) should be("name=test.")
  }
