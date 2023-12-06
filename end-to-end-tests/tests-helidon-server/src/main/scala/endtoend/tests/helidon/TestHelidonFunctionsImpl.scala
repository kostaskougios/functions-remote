package endtoend.tests.helidon
import endtoend.tests.helidon.model.Return1

class TestHelidonFunctionsImpl extends TestsHelidonFunctions:
  override def noArgs(): Int = 5

  override def noArgsUnitReturnType(): Unit = ()

  override def add(a: Int, b: Int): Int = a + b

  override def unitResult(a: Int, b: Int): Unit = ()

  override def addR(a: Int, b: Int): Return1 = Return1(a + b)

  override def addLR(a: Int, b: Int): List[Return1] = List(Return1(a + b))

  override def divide(a: Int, b: Int): Either[Int, String] =
    try Left(a / b)
    catch case e: Throwable => Right(e.getMessage)

  override def alwaysFails(a: Int): String = throw new IllegalArgumentException(s"this method always fails. a=$a")

  override def addParamsEmptySecond(a: Int, l: Long, s: String)(): Int = a + l.toInt + s.toInt

  override def addParams(a: Int, l: Long, s: String)(b: Int): Int = a + l.toInt + s.toInt + b

  override def aBigMsg(in: Seq[Int]): Int = in.sum
