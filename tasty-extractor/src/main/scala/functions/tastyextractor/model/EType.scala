package functions.tastyextractor.model

import org.apache.commons.lang3

case class EType(name: String, code: String, typeArgs: Seq[EType], framework: Option[DetectedFramework], scalaDocs: Option[String], methods: Seq[EMethod]):
  def frameworkName: String  = framework.map(_.frameworkName).getOrElse("none")
  def simplifiedCode: String = if typeArgs.isEmpty then name else s"$name[${typeArgs.map(_.simplifiedCode).mkString(", ")}]"

  /** drops the framework type (if it is present). i.e. if rTpe = F[Int], then F is dropped and Int is returned
    */
  def typeNoFramework(rTpe: EType): EType = framework match
    case Some(DetectedCatsEffect(typeArg, _, _)) if rTpe.name == typeArg => rTpe.typeArgs.head
    case _                                                               => rTpe

object EType:
  def code(name: String, code: String) = EType(name, code, Nil, None, None, Nil)
