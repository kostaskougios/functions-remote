package org.simplified.templates

import org.apache.commons.lang3.StringUtils

class ScalaFileTemplates:
  def apply(code: String, vals: Product): String =
    val lines = applyToBlock(StringUtils.split(code, '\n'), vals)
    lines.mkString("\n")

  private def applyToBlock(lines: Array[String], vals: Product): Array[String] =
    val (search, replace) = searchReplace(vals)
    lines.map: line =>
      StringUtils.replaceEach(line, search, replace)

  private def searchReplace(vals: Product) =
    val search  = vals.productElementNames.map(k => s"`$k`").toArray
    val replace = vals.productIterator.map(_.toString).toArray
    (search, replace)

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
