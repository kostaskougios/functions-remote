package functions.tastyextractor.model

sealed trait DetectedFramework:
  def imports: Seq[String]

case class DetectedCatsEffect(typeArg: String, catsFullClassName: String, catsClassName: String) extends DetectedFramework:
  override def imports = List("cats.effect.*", "cats.syntax.functor.*")
