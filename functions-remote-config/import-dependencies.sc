//> using scala 2.13
//> using dependency org.functions-remote::coursier:0.1-SNAPSHOT
import functions.coursier._

val resolver = new CoursierResolver(".")
resolver.importDependencies("import-dependencies.dep")
println("Ok, functions dependencies imported under .dependencies directory")
