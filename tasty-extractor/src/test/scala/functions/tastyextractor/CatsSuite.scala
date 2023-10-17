package functions.tastyextractor

import functions.tastyextractor.model.DetectedCatsEffect
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class CatsSuite extends AnyFunSuiteLike:
  val e = new StructureExtractor().fromJars(List(Jars.Cats))
  test("type args for type") {
    e.head.types.head.frameworks should be(List(DetectedCatsEffect("F", "cats.effect.kernel.Async")))
  }
