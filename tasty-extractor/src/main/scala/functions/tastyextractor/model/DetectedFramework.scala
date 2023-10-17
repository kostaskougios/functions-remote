package functions.tastyextractor.model

trait DetectedFramework

case class DetectedCatsEffect(typeArg: String, catsFullClassName: String, catsClassName: String) extends DetectedFramework
