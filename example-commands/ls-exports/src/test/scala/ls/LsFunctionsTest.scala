package ls

import ls.LsFunctionsMethods.Ls
import ls.model.{LsFile, LsOptions, LsResult}
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.*

class LsFunctionsTest extends AnyFunSuiteLike:
  val serializer = new LsFunctionsAvroSerializer
  val someResult = LsResult(Seq(LsFile("f1"), LsFile("f2")))

  test("caller end to end") {
    new CallerApp:

      override def transport(method: String, input: Array[Byte]) =
        serializer.lsReturnTypeSerializer(someResult)

      caller.ls("/tmp") should be(someResult)
  }

  test("receiver end to end") {
    val lsFunctions = new UnimplementedLsFunctions:
      override def ls(path: String, lsOptions: LsOptions) =
        path should be("/tmp")
        lsOptions should be(LsOptions.Defaults)
        someResult

    new ReceiverApp(lsFunctions):
      val received = receiver.ls(serializer.lsSerializer(Ls("/tmp", LsOptions.Defaults)))
      serializer.lsReturnTypeDeserializer(received) should be(someResult)
  }

  abstract class CallerApp:
    def transport(method: String, input: Array[Byte]): Array[Byte]
    val caller = LsFunctionsCallerAvroSerializedFactory.createLsFunctionsCaller(transport)

  abstract class ReceiverApp(functions: LsFunctions):
    val receiver = LsFunctionsReceiverAvroSerializedFactory.createLsFunctionsReceiver(functions)
