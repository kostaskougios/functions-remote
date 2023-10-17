package functions.tastyextractor.model

trait DetectedFramework

case class DetectedCatsEffect(typeArg: String, catsClass: String) extends DetectedFramework
