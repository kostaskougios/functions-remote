package functions.coursier
import coursier._
import functions.coursier.utils.Env.FunctionsHome

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import scala.io.Source
import scala.util.Using

/** see https://get-coursier.io/docs/api
  */
class CoursierResolver(functionsHome: String = FunctionsHome) {
  def resolve(dependency: Dependency) = Fetch()
    .addDependencies(dependency)
    .run()

  def importFunctions(depFile: String) = {
    val depF      = new File(depFile)
    val targetDir = new File(functionsHome + s"/.local/.dependencies")
    targetDir.mkdirs()
    println(s"Importing from ${depF.getAbsolutePath} to ${targetDir.getAbsolutePath}")

    val deps = Using.resource(Source.fromFile(depF, "UTF-8"))(_.getLines().toList)

    for {
      dep <- deps.filterNot(_.isBlank)
    } {
      print(s"Importing $dep ... ")
      val Array(groupId, artifactId, version) = dep.split(":")
      val d                                   = Dependency(Module(Organization(groupId), ModuleName(artifactId)), version)
      val r                                   = resolve(d)
      val output                              = r.mkString("\n")
      Files.write(new File(targetDir, s"$dep.classpath").toPath, output.getBytes(StandardCharsets.UTF_8))
      println(s"${r.size} jars")
    }
  }
}
