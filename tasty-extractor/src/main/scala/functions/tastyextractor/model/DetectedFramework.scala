package functions.tastyextractor.model

trait DetectedFramework:
  def frameworkName: String

case class DetectedCatsEffect(typeArg: String, catsFullClassName: String, catsClassName: String) extends DetectedFramework:
  override def frameworkName = "Cats-Effect"
