package functions.coursier

object Resolver {
  private val coursierResolver                                            = new CoursierResolver()
  def createDependenciesForArtifact(artifact: String): Seq[String]        = createDependenciesForArtifacts(Seq(artifact))
  def createDependenciesForArtifacts(artifacts: Seq[String]): Seq[String] = coursierResolver.createDependenciesForArtifacts(artifacts)
}
