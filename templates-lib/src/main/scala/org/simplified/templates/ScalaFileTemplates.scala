package org.simplified.templates

import org.apache.commons.lang3.StringUtils

class ScalaFileTemplates:
  def apply(code: String, vals: Map[String, Any]): String =
    val search  = vals.keys.map(k => s"`$k`").toArray
    val replace = vals.values.toArray.map(_.toString)
    val lines   = StringUtils
      .split(code, '\n')
      .map: line =>
        StringUtils.replaceEach(line, search, replace)
    lines.mkString("\n")

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
