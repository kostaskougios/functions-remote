//> using scala 2.13
//> using dependency org.functions-remote::coursier:0.1-SNAPSHOT
import functions.coursier._

val resolver = new CoursierResolver(".")
resolver.importFunctions("import-functions.dep")
println("Ok, functions dependencies imported under .dependencies directory")
