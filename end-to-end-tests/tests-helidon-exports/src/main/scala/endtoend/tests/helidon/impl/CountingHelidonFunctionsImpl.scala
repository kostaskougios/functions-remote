package endtoend.tests.helidon.impl

import endtoend.tests.helidon.TestsHelidonFunctions
import endtoend.tests.helidon.model.Return1

import java.util.concurrent.atomic.AtomicInteger

// just for testing purposes, normally the impl should not be in this module
class CountingHelidonFunctionsImpl extends TestsHelidonFunctions:
  val noArgsC                                       = new AtomicInteger(0)
  override def noArgs(): Int                        =
    noArgsC.incrementAndGet()
    5
  val noArgsUnitReturnTypeC                         = new AtomicInteger(0)
  override def noArgsUnitReturnType(): Unit         =
    noArgsUnitReturnTypeC.incrementAndGet()
    ()
  override def add(a: Int, b: Int): Int             = a + b
  val unitResultC                                   = new AtomicInteger(0)
  override def unitResult(a: Int, b: Int): Unit     =
    unitResultC.incrementAndGet()
    ()
  override def addR(a: Int, b: Int): Return1        = Return1(a + b)
  override def addLR(a: Int, b: Int): List[Return1] = List(Return1(a + b))

  override def divide(a: Int, b: Int): Either[Int, String] =
    try Left(a / b)
    catch case e: Throwable => Right(e.getMessage)

  override def alwaysFails(a: Int): String                             = throw new RuntimeException(s"this method always fails. a=$a")
  override def addParamsEmptySecond(a: Int, l: Long, s: String)(): Int = a + l.toInt + s.toInt
  override def addParams(a: Int, l: Long, s: String)(b: Int): Int      =
    val r = a + l.toInt + s.toInt + b
    r
