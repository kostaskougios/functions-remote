package functions.tastyextractor.model

import org.apache.commons.lang3
import org.apache.commons.lang3.StringUtils

case class EType(name: String, code: String, typeArgs: Seq[EType], framework: Option[DetectedFramework], scalaDocs: Option[String], methods: Seq[EMethod]):
  def frameworkName: String = framework.map(_.frameworkName).getOrElse("none")

  def breakdown: ETypeBreakdown =
    val tpe        = if code.contains('[') then StringUtils.substringBefore(code, "[") else code
    val components = tpe.split("\\.").toList.reverse
    ETypeBreakdown(components.tail.reverse, components.head, Option(StringUtils.substringBetween(code, "[", "]")))

case class ETypeBreakdown(packages: List[String], name: String, typeArgs: Option[String]):
  def codeNoTypeArgs: String = packages.mkString(".") + "." + name
  def codeNoPackages: String =
    val ta = typeArgs.map(a => s"[$a]").getOrElse("")
    name + ta

object EType:
  def code(name: String, code: String) = EType(name, code, Nil, None, None, Nil)
