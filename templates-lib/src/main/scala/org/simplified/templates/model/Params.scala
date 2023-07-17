package org.simplified.templates.model

case class Params(params: Seq[Param])

object Params:
  def of(p: Param*): Params = Params(p.toList)

case class Param(name: String, `type`: String)
