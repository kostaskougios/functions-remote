package functions.utils

import java.io.File
import scala.io.Source
import scala.util.Using

class FileUtils:
  def readFile(file: File): Seq[String] = Using.resource(Source.fromFile(file, "UTF-8"))(_.getLines().toList)

object FileUtils extends FileUtils
