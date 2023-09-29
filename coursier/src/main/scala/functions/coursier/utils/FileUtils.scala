package functions.coursier.utils

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import scala.io.Source
import scala.util.Using

object FileUtils {
  def writeTextFile(targetDir: File, fileName: String, content: String) =
    Files.write(new File(targetDir, fileName).toPath, content.getBytes(StandardCharsets.UTF_8))

  def readTextFile(f: File): Seq[String] = Using.resource(Source.fromFile(f, "UTF-8"))(_.getLines().toList)
}
