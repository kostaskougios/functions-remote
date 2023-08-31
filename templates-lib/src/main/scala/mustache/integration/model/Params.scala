package mustache.integration.model

case class Params(params: Seq[Param]):
  def toMethodDeclArguments: String = params.map(_.toMethodDeclArguments).mkString(", ")
  def toMethodCallArguments: String = params.map(_.name).mkString(", ")

case class Param(name: String, `type`: String):
  def toMethodDeclArguments: String = s"$name:${`type`}"
