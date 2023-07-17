package org.simplified.templates

import org.apache.commons.lang3.StringUtils

class ScalaFileTemplates:
  def apply(code: String, vals: Product): String =
    val search  = vals.productElementNames.map(k => s"`$k`").toArray
    val replace = vals.productIterator.map(_.toString).toArray
    val lines   = StringUtils
      .split(code, '\n')
      .map: line =>
        StringUtils.replaceEach(line, search, replace)
    lines.mkString("\n")

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
