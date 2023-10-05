//> using scala 2.13
//> using dependency org.functions-remote::coursier:0.1-SNAPSHOT
import functions.coursier._

val TestsVersion = "0.1-SNAPSHOT"

val functionsDependencies = Seq(
    s"functions.end-to-end-tests:tests-impl_3:$TestsVersion"
)
val exportsDependencies   = Seq(
    s"functions.end-to-end-tests:tests-exports_3:$TestsVersion"
)
val resolver = new CoursierResolver(".")
resolver.importDependencies(functionsDependencies)
resolver.importExports(exportsDependencies)

println("Ok, functions dependencies imported under .local/dependencies directory and exports under .local/exports")
