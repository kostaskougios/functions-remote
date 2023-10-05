val scala3Version = "3.3.1"

ThisBuild / version := "0.1-SNAPSHOT"

ThisBuild / organization := "org.functions-remote"

name := "functions-remote"

ThisBuild / scalaVersion := scala3Version

ThisBuild / scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-Xmax-inlines", "64")

val Scala3Compiler = "org.scala-lang" %% "scala3-compiler"        % scala3Version
val Scala3Tasty    = "org.scala-lang" %% "scala3-tasty-inspector" % scala3Version

val ScalaTest = "org.scalatest" %% "scalatest" % "3.2.15" % Test
val Diffx     = Seq(
  "com.softwaremill.diffx" %% "diffx-core",
  "com.softwaremill.diffx" %% "diffx-scalatest-should"
).map(_ % "0.7.1" % Test)

val Logback     = "ch.qos.logback"     % "logback-classic" % "1.4.6"
val CommonsText = "org.apache.commons" % "commons-text"    % "1.10.0"
val CommonsIO   = "commons-io"         % "commons-io"      % "2.11.0"

val Avro4s   = "com.sksamuel.avro4s"              %% "avro4s-core" % "5.0.5"
val Mustache = "com.github.spullara.mustache.java" % "compiler"    % "0.9.10"
val Coursier = "io.get-coursier" %% "coursier" % "2.1.7" // .cross(CrossVersion.for3Use2_13).exclude("org.scala-lang.modules", "scala-xml_2.13")

val CirceVersion = "0.14.1"

val Circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % CirceVersion)

val commonSettings = Seq(
)

lazy val `runtime-and-generator-common` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )

lazy val `proxy-generator-common` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )
  .dependsOn(`runtime-and-generator-common`)

lazy val `tasty-extractor` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(Scala3Tasty, ScalaTest, CommonsIO, CommonsText) ++ Diffx
  )
  .dependsOn(`proxy-generator-common`)

lazy val coursier = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(Coursier, ScalaTest),
    scalaVersion                           := "2.13.12",
    scalacOptions -= "-Xmax-inlines",
    Compile / packageDoc / publishArtifact := false
  )

lazy val `proxy-generator` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )
  .dependsOn(`templates-lib`, `tasty-extractor`, `proxy-generator-common`)
  .enablePlugins(PackPlugin)

lazy val `templates-lib` = project.settings(
  commonSettings,
  libraryDependencies ++= Seq(ScalaTest, CommonsIO, CommonsText, Mustache) ++ Diffx
)

lazy val `functions-common` = project.settings(commonSettings).dependsOn(`runtime-and-generator-common`)

lazy val `functions-discovery` = project.settings(commonSettings).dependsOn(`functions-common`)

lazy val `functions-invoker` = project.settings(commonSettings).dependsOn(`functions-common`)

// ----------------------- end to end test modules --------------------------------
val endToEndTestsSettings = Seq(
  organization := "functions.end-to-end-tests",
  version      := "0.1-SNAPSHOT"
)

lazy val `tests-exports` = project
  .in(file("end-to-end-tests/tests-exports"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest),
    buildInfoKeys    := Seq[BuildInfoKey](organization, name, version, scalaVersion, "exportedArtifact" -> "tests-impl_3"),
    buildInfoPackage := "endtoend.tests"
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `tests-impl` = project
  .in(file("end-to-end-tests/tests-impl"))
  .settings(
    endToEndTestsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe
  )
  .dependsOn(`tests-exports`, `functions-invoker`)

lazy val `using-tests` = project
  .in(file("end-to-end-tests/using-tests"))
  .settings(
    endToEndTestsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe
  )
  .dependsOn(`tests-exports`, `functions-discovery`)

// ----------------------- Example commands ---------------------------------------
val exampleCommandsSettings = Seq(
  organization := "com.example",
  version      := "0.1-SNAPSHOT"
)
lazy val `ls-exports`       = project
  .in(file("example-commands/ls-exports"))
  .settings(
    exampleCommandsSettings,
    libraryDependencies ++= Seq(ScalaTest),
    buildInfoKeys    := Seq[BuildInfoKey](organization, name, version, scalaVersion, "exportedArtifact" -> "ls_3"),
    buildInfoPackage := "commands.ls"
  )
  .enablePlugins(BuildInfoPlugin)

lazy val ls = project
  .in(file("example-commands/ls"))
  .settings(
    exampleCommandsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s) ++ Circe
  )
  .dependsOn(`ls-exports`, `functions-invoker`)

lazy val `using-commands` = project
  .in(file("example-commands/using-commands"))
  .settings(
    exampleCommandsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s) ++ Circe
  )
  .dependsOn(`ls-exports`, `functions-discovery`)
