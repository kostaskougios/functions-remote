package functions.tastyextractor

import functions.tastyextractor.model.EBuilders.eType
import functions.tastyextractor.model.{DetectedCatsEffect, EImport, EType}
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class CatsStructureExtractorSuite extends AnyFunSuiteLike:
  val e                         = new StructureExtractor().fromJars(List(Jars.Cats))
  val testsCatsFunctions        = e.find(_.types.exists(_.name == "TestsCatsFunctions")).get
  val testsCatsFunctionsTrait   = testsCatsFunctions.types.head
  val testsCatsFunctionsImports = testsCatsFunctions.imports
  val catsAdd                   = testsCatsFunctionsTrait.methods.find(_.name == "catsAdd").get
  val catsAddR                  = testsCatsFunctionsTrait.methods.find(_.name == "catsAddR").get
  val catsAddLR                 = testsCatsFunctionsTrait.methods.find(_.name == "catsAddLR").get
  val noCatsDivide              = testsCatsFunctionsTrait.methods.find(_.name == "noCatsDivide").get

  test("detects cats effect") {
    e.head.types.head.framework should be(Some(DetectedCatsEffect("F", "cats.effect.kernel.Async", "Async")))
  }

  test("imports not including F") {
    testsCatsFunctionsImports should not contain EImport("TestsCatsFunctions.this.F")
  }

  test("imports including normal imports") {
    testsCatsFunctionsImports should contain(EImport("endtoend.tests.cats.model.Return1"))
    testsCatsFunctionsImports should contain(EImport("cats.effect.kernel.Async"))
  }

  test("method cats return simple type") {
    catsAdd.returnType should be(eType("F", "TestsCatsFunctions.this.F[scala.Int]", Seq(eType("Int", "scala.Int"))))
  }

  test("simplifiedCode for method cats return simple type") {
    catsAdd.returnType.simplifiedCode should be("F[Int]")
  }

  test("method cats return type with case class type arg") {
    catsAddR.returnType should be(
      eType("F", "TestsCatsFunctions.this.F[endtoend.tests.cats.model.Return1]", Seq(eType("Return1", "endtoend.tests.cats.model.Return1")))
    )
  }

  test("simplifiedCode for method cats return type with case class type arg") {
    catsAddR.returnType.simplifiedCode should be("F[Return1]")
  }

  test("method cats return type with List of case class type arg") {
    catsAddLR.returnType should be(
      eType(
        "F",
        "TestsCatsFunctions.this.F[scala.collection.immutable.List[endtoend.tests.cats.model.Return1]]",
        Seq(eType("List", "scala.collection.immutable.List[endtoend.tests.cats.model.Return1]", Seq(eType("Return1", "endtoend.tests.cats.model.Return1"))))
      )
    )
  }

  test("simplifiedCode for method cats return type with List of case class type arg") {
    catsAddLR.returnType.simplifiedCode should be("F[List[Return1]]")
  }

  test("method cats return type with 2 type args") {
    noCatsDivide.returnType should be(
      eType("Either", "scala.util.Either[scala.Int, scala.Predef.String]", Seq(eType("Int", "scala.Int"), eType("String", "scala.Predef.String")))
    )
  }
