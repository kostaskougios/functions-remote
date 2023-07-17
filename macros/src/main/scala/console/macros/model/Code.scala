package console.macros.model

import org.apache.commons.io.FileUtils

import java.io.File

trait Code:
  def imports: Set[String]
  def main: String
  def toCode: String

/** A full code file belonging to a package, having imports and code
  */
case class CodeFile(file: String, `package`: EPackage, imports: Set[String], main: String) extends Code:
  override def toCode =
    s"""
       |package ${`package`.name}
       |${imports.map(i => s"import $i").mkString("\n")}
       |
       |$main
       |""".stripMargin

  def writeTo(srcRootFolder: String): Unit =
    val f = new File(srcRootFolder, file)
    f.getParentFile.mkdirs()
    FileUtils.writeStringToFile(f, toCode, "UTF-8")

/** Inner code, meant to be part of i.e. a CodeFile
  */
case class InnerCode(imports: Set[String], main: String) extends Code:
  def +(c: InnerCode): InnerCode = InnerCode(
    imports ++ c.imports,
    s"""
       |$main
       |${c.main}
       |""".stripMargin
  )
  override def toCode            =
    s"""
       |${imports.map(i => s"import $i").mkString("\n")}
       |
       |$main
       |""".stripMargin

object InnerCode:
  def Empty = InnerCode(Set.empty, "")
