package endtoend.tests.helidon

import endtoend.tests.helidon.model.Return1
import functions.helidon.transport.HelidonTransport
import functions.helidon.transport.exceptions.RequestFailedException
import functions.model.Serializer
import functions.model.Serializer.Avro
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import java.util.concurrent.atomic.AtomicInteger

class EndToEndHelidonSuite extends AnyFunSuite:

  def withServer(serializer: Serializer)(f: (TestsHelidonFunctions, CountingHelidonFunctionsImpl) => Unit): Unit =
    val impl       = new CountingHelidonFunctionsImpl
    val avroRoutes = TestsHelidonFunctionsReceiverFactory.newAvroTestsHelidonFunctionsHelidonRoutes(impl)
    val jsonRoutes = TestsHelidonFunctionsReceiverFactory.newJsonTestsHelidonFunctionsHelidonRoutes(impl)
    HelidonServer.withServerDo(0, avroRoutes.routes, jsonRoutes.routes): server =>
      val client    = HelidonClient.newClient(server.port)
      val transport = new HelidonTransport(client)
      val testF     = serializer match
        case Serializer.Avro => TestsHelidonFunctionsCallerFactory.newHelidonAvroTestsHelidonFunctions(transport)
        case Serializer.Json => TestsHelidonFunctionsCallerFactory.newHelidonJsonTestsHelidonFunctions(transport)

      f(testF, impl)

//  test("doesn't open too many sockets") {
//    withServer(Serializer.Avro): (f, _) =>
//      for _ <- 1 to 30_000 do f.add(1, 2)
//  }
  for serializer <- Seq(Serializer.Json, Serializer.Avro) do
    test(s"$serializer : noArgs") {
      withServer(serializer): (f, i) =>
        f.noArgs() should be(5)
        i.noArgsC.get should be(1)
    }

    test(s"$serializer : noArgsUnitReturnType") {
      withServer(serializer): (f, i) =>
        f.noArgsUnitReturnType() should be(())
        i.noArgsUnitReturnTypeC.get should be(1)
    }

    test(s"$serializer : add") {
      withServer(serializer): (f, _) =>
        f.add(1, 2) should be(3)
    }

    test(s"$serializer : unitResult") {
      withServer(serializer): (f, i) =>
        f.unitResult(1, 2) should be(())
        i.unitResultC.get should be(1)
    }

    test(s"$serializer : addR") {
      withServer(serializer): (f, _) =>
        f.addR(1, 2) should be(Return1(3))
    }

    test(s"$serializer : addLR") {
      withServer(serializer): (f, _) =>
        f.addLR(1, 2) should be(Seq(Return1(3)))
    }

    test(s"$serializer : divide left") {
      withServer(serializer): (f, _) =>
        f.divide(10, 5) should be(Left(2))
    }

    test(s"$serializer : divide right") {
      withServer(serializer): (f, _) =>
        f.divide(10, 0) should be(Right("/ by zero"))
    }

    test(s"$serializer : alwaysFails") {
      withServer(serializer): (f, _) =>
        a[RequestFailedException] should be thrownBy f.alwaysFails(1)
    }

    test(s"$serializer : addParamsEmptySecond") {
      withServer(serializer): (f, _) =>
        f.addParamsEmptySecond(1, 2L, "3")() should be(6)
    }

    test(s"$serializer : addParams") {
      withServer(serializer): (f, _) =>
        f.addParams(1, 2L, "3")(4) should be(10)
    }

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
