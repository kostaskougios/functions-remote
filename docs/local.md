# Calling functions locally but without having them into your app classpath

Let's see the structure of our LsFunctions example. We will impl both the caller and receiver.
All files can be found [here](../example-commands). The build config is the most complicated part
of functions-remote at the moment but it will be simplified in the future.

```
├── functions-remote-config     : contains information on how functions-remote can locate all jars of ls-receiver at runtime
├── ls-caller                   : the caller, depends on ls-exports only but is able to call ls-receiver
├── ls-exports                  : contains the exported traits , LsFunctions
├── ls-receiver                 : contains LsFunctionsImpl and depends on ls-exports
└── scripts                     : scala-cli scripts to help us with the code generation configuration
```

Let's configure sbt first with our modules. We will need build-info sbt plugin, so add this to `plugins.sbt`:

```sbt
addSbtPlugin("com.eed3si9n"   % "sbt-buildinfo" % "0.11.0")
```

then configure the `build.sbt`. Note we need to have src folder for the generated classes.

```sbt
val FunctionsInvokerVersion = the version of the lib
val FunctionsInvoker = "org.functions-remote" %% "functions-invoker" % FunctionsInvokerVersion
val Avro4s   = "com.sksamuel.avro4s"              %% "avro4s-core" % "5.0.5"
val CirceVersion = "0.14.1"

val Circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % CirceVersion)

lazy val `ls-exports`       = project
  .settings(
    libraryDependencies ++= Seq(ScalaTest),
    // make sure we have the exportedArtifact key
    buildInfoKeys    := Seq[BuildInfoKey](organization, name, version, scalaVersion, "exportedArtifact" -> "ls-receiver_3"),
    buildInfoPackage := "commands.ls"
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `ls-receiver` = project
  .settings(
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, FunctionsInvoker) ++ Circe
  )
  .dependsOn(`ls-exports`, `functions-invoker`)

lazy val `ls-caller` = project
  .settings(
    exampleCommandsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, FunctionsInvoker) ++ Circe
  )
  .dependsOn(`ls-exports`)
```

Now we will need to create our exported trait and it's related classes inside `ls-exports`, 
see the full code [here](../example-commands/ls-exports). Note the `//> exported`, this marks
our trait as an exported trait, without this comment the trait is not going to be picked by
the tasty analyser and not exported.

```scala
/** The exported functions of ls module
  *
  * This marks this trait as exported : //> exported
  */
trait LsFunctions:
  def ls(path: String, lsOptions: LsOptions = LsOptions.Defaults): LsResult
  def fileSize(path: String): Long

case class LsOptions(includeDirs: Boolean = true)
case class LsResult(files: Seq[LsFile])
case class LsFile(name: String)
.. etc ..
```

The tasty parser needs a locally published jar, so lets do that:

```
sbt ls-exports/publishLocal
```

## Using an isolated classloader

Now locally we have the jars and are ready to use the code generator. Because the generator is impl in scala 3, we can't use it
within sbt, so we have to i.e. use `scala-cli` (or just some code in a separate module). 
The `scala-cli` scripts are [here](../example-commands/scripts). For example the generator for the caller:


`ls-caller.sc`:
```scala
import functions.proxygenerator.*

val LsExportsDep = "com.example:ls-exports_3:0.1-SNAPSHOT"
val TargetRoot = s"$ProjectRoot/ls-caller/src/main/generated"

generateCaller(generatorConfig,avroSerialization = true, jsonSerialization = true, classloaderTransport = true)
  .generate(TargetRoot, LsExportsDep)
```
We generate a caller with support for avro and json (circe) serialization. We also generate code needed for the isolated
classloader transport. This transport allows us to call `ls-receiver` without having it as a dependency of `ls-caller`.

On the receiver side we need something like this:


`ls-receiver.sc`:
```scala
import functions.proxygenerator.*

val LsExportsDep = "com.example:ls-exports_3:0.1-SNAPSHOT"
val TargetRoot = s"$ProjectRoot/ls-receiver/src/main/generated"

generateReceiver(generatorConfig, avroSerialization = true, jsonSerialization = true)
    .generate(TargetRoot, LsExportsDep)
```
We can run the scripts in the terminal:

```shell
cd <project root directory>
scala-cli scripts scripts/ls-receiver.sc
scala-cli scripts scripts/ls-caller.sc
```

We now have all the generated classes for both the caller and receiver and we can use them to do actual calls.

For the isolated classloader transport to work, on the receiver end we need to wire it with a fixed name class
[`exported.Exported`](../example-commands/ls-receiver/src/main/scala/exported/Exported.scala).
This class will be invoked by the transport. Every call has `Coordinates3(className, method, serializer)` which specify
which method to call and how the data are serialized. But please note no reflection is used, all calls are via generated code.

```scala
object Exported extends BiFunction[String, Array[Byte], Array[Byte]]:
  private val impl      = new LsFunctionsImpl
  private val functions = LsFunctionsReceiverFactory.invokerMap(impl)

  override def apply(coordinates: String, data: Array[Byte]): Array[Byte] =
    functions(Coordinates3(coordinates)).apply(data)
```



## Using a separate jvm per call
Not yet implemented
