package console.macros.model

case class EPackage(name: String, types: Seq[EType]):
  def toPath: String = name.replace('.', '/')
