package functions.fibers

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

import java.util.concurrent.ExecutionException

class FiberExecutorTest extends AnyFunSuiteLike:
  test("get()") {
    FiberExecutor.withFiberExecutor: executor =>
      executor.submit { 5 }.get() should be(5)
  }

  test("get() when exception") {
    FiberExecutor.withFiberExecutor: executor =>
      an[ExecutionException] should be thrownBy
        executor
          .submit {
            throw new IllegalArgumentException()
          }
          .get()
  }

  test("two() passes the correct fibers to each fiber") {
    FiberExecutor.withFiberExecutor: executor =>
      val fibers = executor.two(
        _.fiber1,
        _.fiber2
      )

      fibers.get() should be((fibers.fiber1, fibers.fiber2))
  }
  test("two() fibers always can access their fiber and the other fiber") {
    for _ <- 1 to 100000 do
      FiberExecutor.withFiberExecutor: executor =>
        val fibers = executor.two[Int, String](
          tf =>
            tf.fiber1 should not be null
            tf.fiber2 should not be null
            5
          ,
          tf =>
            tf.fiber1 should not be null
            tf.fiber2 should not be null
            "a"
        )

        fibers.get() should be((5, "a"))
  }

  test("TwoFibers interrupt") {
    FiberExecutor.withFiberExecutor: executor =>
      val fibers = executor.two(
        _ => Thread.sleep(1000),
        _ => Thread.sleep(1000)
      )
      val start  = System.currentTimeMillis()
      Thread.sleep(50)
      fibers.interrupt()
      fibers.await()
      (System.currentTimeMillis() - start) should be < 200L
  }
