package org.simplified.templates

import org.apache.commons.lang3.StringUtils

class ScalaFileTemplates:
  private val ForEach                            = "// = foreach "
  def apply(code: String, vals: Product): String =
    val (search, replace) = searchReplace(vals)
    val lines             = applyToBlock(StringUtils.split(code, '\n').toList, search, replace)
    lines.mkString("\n")

  private def applyToBlock(lines: List[String], search: Array[String], replace: Array[String]): List[String] =
    lines match
      case Nil       => Nil
      case line :: l =>
        StringUtils.replaceEach(line, search, replace) :: applyToBlock(l, search, replace)

  private def searchReplace(vals: Product) =
    val search  = vals.productElementNames.map(k => s"`$k`").toArray
    val replace = vals.productIterator.map(_.toString).toArray
    (search, replace)

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
