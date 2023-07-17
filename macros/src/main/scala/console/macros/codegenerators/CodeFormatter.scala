package console.macros.codegenerators

object CodeFormatter {
  def tabs(tabs: Int, lines: Seq[String]): Seq[String] =
    val t = "  " * tabs
    lines.flatMap(_.split("\n")).map(t + _)
}
