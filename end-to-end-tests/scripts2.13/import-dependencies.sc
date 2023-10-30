//> using scala 2.13
//> using dependency org.functions-remote::coursier:0.1-SNAPSHOT
import functions.coursier.Resolver._

val TestsVersion = "0.1-SNAPSHOT"

val files = createDependenciesForArtifacts(
    Seq(
        s"functions.end-to-end-tests:tests-impl_3:$TestsVersion",
        s"functions.end-to-end-tests:tests-cats-impl_3:$TestsVersion",
        s"functions.end-to-end-tests:tests-exports_3:$TestsVersion",
        s"functions.end-to-end-tests:tests-cats-exports_3:$TestsVersion"
    )
)

println(s"Created dependency text files:\n${files.mkString("\n")}")