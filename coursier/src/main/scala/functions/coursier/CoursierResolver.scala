package functions.coursier
import coursier._
import functions.coursier.utils.Env.FunctionsHome
import functions.coursier.utils.FileUtils

import java.io.File

/** see https://get-coursier.io/docs/api
  */
class CoursierResolver(functionsHome: String = FunctionsHome) {
  println(s"functions-remote config dir is $functionsHome")

  def createDependencyFileForExports(artifacts: Seq[String]): Unit =
    for (artifact <- artifacts) {
      print(s"Importing exports dependencies from $artifact ... ")
      val depFile   = resolve(toDependency(artifact)).head
      val targetDir = new File(functionsHome + "/.local/exports")
      targetDir.mkdirs()
      FileUtils.writeTextFile(targetDir, s"$artifact.export", depFile.getAbsolutePath)
      println(s"Created dependencies text file $targetDir/$artifact.export")
    }

  def createDependenciesForArtifact(artifact: String): Unit = createDependenciesForArtifacts(Seq(artifact))

  def createDependenciesForArtifacts(artifacts: Seq[String]): Unit = {
    val targetDir = new File(functionsHome + "/.local/dependencies")
    targetDir.mkdirs()
    println(s"Importing to directory ${targetDir.getAbsolutePath}")
    for (artifact <- artifacts) {
      print(s"Importing $artifact ... ")
      val d      = toDependency(artifact)
      val r      = resolve(d)
      val output = r.mkString("\n")
      FileUtils.writeTextFile(targetDir, s"$artifact.classpath", output)
      println(s"${r.size} jars")
    }
  }

  private def resolve(dependency: Dependency) = Fetch()
    .addDependencies(dependency)
    .run()

  private def toDependency(dep: String) = {
    val (groupId, artifactId, version) = dep.split(":") match {
      case Array(groupId, artifactId, version) => (groupId, artifactId, version)
      case _ => throw new IllegalArgumentException(s"Can't parse dependency $dep, it should be in the format of group:artifact:version")
    }
    Dependency(Module(Organization(groupId), ModuleName(artifactId)), version)
  }
}
