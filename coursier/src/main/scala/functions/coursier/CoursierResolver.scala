package functions.coursier
import coursier._
import coursier.params.ResolutionParams

// see https://get-coursier.io/docs/api
class CoursierResolver {
  def resolve(dependency: Dependency) = {
//    val params = ResolutionParams()
//      .withScalaVersion("3.3.1")
//    Resolve()
//      .addDependencies(dependency)
//      .withResolutionParams(params)
//      .run()
    Fetch()
      .addDependencies(dependency)
      .run()
  }
}

object CoursierResolver extends App {
  val resolver = new CoursierResolver
  val d        = dep"org.functions-remote:proxy-generator_3:0.1-SNAPSHOT"
  val r        = resolver.resolve(d)
  println(r.mkString("\n"))
}
