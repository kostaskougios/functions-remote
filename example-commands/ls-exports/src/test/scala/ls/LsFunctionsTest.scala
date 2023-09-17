package ls

import ls.model.{LsFile, LsResult}
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class LsFunctionsTest extends AnyFunSuiteLike:
  val serializer = new LsFunctionsAvroSerializer
  test("caller end to end") {
    new CallerApp:
      val result = LsResult(Seq(LsFile("f1"), LsFile("f2")))

      override def transport(method: String, input: Array[Byte]) =
        serializer.lsReturnTypeSerializer(result)

      caller.ls("/tmp") should be(result)
  }
  abstract class CallerApp:
    def transport(method: String, input: Array[Byte]): Array[Byte]
    val caller = LsFunctionsCallerAvroSerializedFactory.createLsFunctionsCaller(transport)
