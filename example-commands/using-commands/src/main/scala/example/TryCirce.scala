package example
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import commands.ls.LsFunctionsMethods.Ls
import commands.model.LsOptions

// see https://circe.github.io/circe/
@main def tryCirce(): Unit =
  val j       = Ls("/tmp/path", LsOptions()).asJson.noSpaces
  println(j)
  val decoder = Decoder[Ls]
  val decoded = parse(j).flatMap(decoder.decodeJson)
  println(decoded)
