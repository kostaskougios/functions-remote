package functions.tastyextractor.model

import org.apache.commons.lang3
import org.apache.commons.lang3.StringUtils

case class EType(name: String, code: String, typeArgs: Seq[EType], framework: Option[DetectedFramework], scalaDocs: Option[String], methods: Seq[EMethod]):
  def frameworkName: String  = framework.map(_.frameworkName).getOrElse("none")
  def simplifiedCode: String = if typeArgs.isEmpty then name else s"$name[${typeArgs.map(_.simplifiedCode).mkString(", ")}]"

object EType:
  def code(name: String, code: String) = EType(name, code, Nil, None, None, Nil)
