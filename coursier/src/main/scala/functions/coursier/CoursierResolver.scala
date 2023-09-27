package functions.coursier
import coursier._

class CoursierResolver {
  def resolve(group: String, artifact: String, version: String) = {
    Resolve()
      .addDependencies(Dependency(Module(Organization(group), ModuleName(artifact)), version))
      .run()
  }
}

object CoursierResolver extends App {
  val resolver = new CoursierResolver
  val r        = resolver.resolve("org.functions-remote", "proxy-generator_3", "0.1-SNAPSHOT")
  println(r.dependencies.mkString("\n"))
}
