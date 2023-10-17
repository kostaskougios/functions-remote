package functions.tastyextractor.model

object EBuilders:
  def eType(
      name: String = "X",
      code: String = "scala.X",
      typeArgs: Seq[EType] = Nil,
      framework: Option[DetectedFramework] = None,
      scalaDocs: Option[String] = None,
      methods: Seq[EMethod] = Nil
  ) =
    EType(name, code, typeArgs, framework, scalaDocs, methods)
