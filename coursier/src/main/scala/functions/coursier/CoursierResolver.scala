package functions.coursier
import coursier._
import coursier.params.ResolutionParams
import functions.coursier.utils.Env.FunctionsHome

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import scala.io.Source
import scala.util.Using

// see https://get-coursier.io/docs/api
class CoursierResolver {
  def resolve(dependency: Dependency) = Fetch()
    .addDependencies(dependency)
    .run()
}

object CoursierResolver extends App {
  val resolver = new CoursierResolver
  val deps     = Using.resource(Source.fromFile(new File(FunctionsHome + "/import-functions.dep"), "UTF-8"))(_.getLines().toList)

  for {
    dep <- deps.filterNot(_.isBlank)
  } {
    print(s"Importing $dep ... ")
    val Array(groupId, artifactId, version) = dep.split(":")
    val d                                   = Dependency(Module(Organization(groupId), ModuleName(artifactId)), version)
    val r                                   = resolver.resolve(d)
    val output                              = r.mkString("\n")
    Files.write(new File(FunctionsHome + s"/.dependencies/$dep.classpath").toPath, output.getBytes(StandardCharsets.UTF_8))
    println(s"${r.size} jars")
  }
}
