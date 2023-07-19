package org.simplified.templates.model

case class Params(params: Seq[Param]):
  def toCode: String = params.map(_.toCode).mkString(", ")
  def toMethodArg:String = params.map(_.name).mkString(", ")

object Params:
  def of(p: Param*): Params = Params(p.toList)

case class Param(name: String, `type`: String):
  def toCode: String = s"$name:${`type`}"
