package functions.coursier

object Resolver {
  private val coursierResolver = new CoursierResolver()

  def createDependencyFileForExport(artifact: String): Unit        = createDependencyFileForExports(Seq(artifact))
  def createDependencyFileForExports(artifacts: Seq[String]): Unit = coursierResolver.createDependencyFileForExports(artifacts)

  def createDependenciesForArtifact(artifact: String): Unit        = createDependenciesForArtifacts(Seq(artifact))
  def createDependenciesForArtifacts(artifacts: Seq[String]): Unit = coursierResolver.createDependenciesForArtifacts(artifacts)
}
