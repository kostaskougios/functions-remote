package tryit

import org.apache.commons.io.FileUtils

import java.io.File

def deleteScalaFiles(dir: String) =
  val f = new File(dir)
  println(s"Deleting scala files from ${f.getAbsolutePath}")
  FileUtils.deleteDirectory(f)
