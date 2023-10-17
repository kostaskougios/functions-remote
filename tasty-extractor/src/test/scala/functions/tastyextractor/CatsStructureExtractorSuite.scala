package functions.tastyextractor

import functions.tastyextractor.model.{DetectedCatsEffect, EImport}
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class CatsStructureExtractorSuite extends AnyFunSuiteLike:
  val e       = new StructureExtractor().fromJars(List(Jars.Cats))
  val imports = e.flatMap(_.imports)

  test("detects cats effect") {
    e.head.types.head.frameworks should be(List(DetectedCatsEffect("F", "cats.effect.kernel.Async", "Async")))
  }

  test("imports not including F") {
    imports should not contain EImport("TestsCatsFunctions.this.F")
  }

  test("imports including normal imports") {
    imports should contain(EImport("endtoend.tests.cats.model.Return1"))
    imports should contain(EImport("cats.effect.kernel.Async"))
  }
