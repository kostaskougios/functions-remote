package codegen.tastyextractor.model

case class EType(name: String, code: String, methods: Seq[EMethod]):
  def typesInMethods: Seq[String] = methods.flatMap(m => m.paramss.flatMap(_.map(_.`type`) :+ m.returnType.code)).distinct.filter(_.contains('.'))

object EType:
  def code(name: String, code: String) = EType(name, code, Nil)
