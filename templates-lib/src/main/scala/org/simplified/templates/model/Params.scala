package org.simplified.templates.model

case class Params(params: Seq[Param]):
  def toMethodDeclArguments: String = params.map(_.toMethodDeclArguments).mkString(", ")
  def toMethodCallArguments:String = params.map(_.name).mkString(", ")

object Params:
  def of(p: Param*): Params = Params(p.toList)

case class Param(name: String, `type`: String):
  def toMethodDeclArguments: String = s"$name:${`type`}"
