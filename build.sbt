val scala3Version = "3.3.1"

ThisBuild / version := "0.1-SNAPSHOT"

ThisBuild / organization := "org.functions-remote"

name := "functions-remote"

ThisBuild / scalaVersion := scala3Version

ThisBuild / scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-Xmax-inlines", "64")

// ----------------------- dependencies --------------------------------

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

val CirceVersion = "0.14.1"

val Circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % CirceVersion)

val Http4sVersion = "0.23.23"

val Http4sServer = Seq(
  "org.http4s" %% "http4s-ember-server" % Http4sVersion,
  "org.http4s" %% "http4s-dsl"          % Http4sVersion
)

val Http4sClient = Seq(
  "org.http4s" %% "http4s-ember-client" % Http4sVersion
)

val Http4sCirce = Seq("org.http4s" %% "http4s-circe" % Http4sVersion)

val CatsEffect = "org.typelevel" %% "cats-effect" % "3.5.2"

val CatsEffectsTesting = "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test
// ----------------------- modules --------------------------------

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

lazy val `proxy-generator` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )
  .dependsOn(`templates-lib`, `tasty-extractor`, `proxy-generator-common`, `functions-common`)
  .enablePlugins(PackPlugin)

lazy val `templates-lib` = project.settings(
  commonSettings,
  libraryDependencies ++= Seq(ScalaTest, CommonsIO, CommonsText, Mustache) ++ Diffx
)

lazy val `functions-common` = project.settings(commonSettings).dependsOn(`runtime-and-generator-common`)

lazy val `functions-caller` = project.settings(commonSettings).dependsOn(`functions-common`)

lazy val `functions-receiver` = project.settings(commonSettings).dependsOn(`functions-common`)

lazy val `http4s-server` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Http4sServer ++ Seq(ScalaTest)
  )
  .dependsOn(`functions-receiver`)

lazy val `http4s-client` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Http4sClient ++ Seq(ScalaTest)
  )
  .dependsOn(`functions-caller`)

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
  .enablePlugins(BuildInfoPlugin, FunctionsRemotePlugin)

lazy val `tests-cats-exports` = project
  .in(file("end-to-end-tests/tests-cats-exports"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, CatsEffect),
    buildInfoKeys    := Seq[BuildInfoKey](organization, name, version, scalaVersion, "exportedArtifact" -> "tests-cats-impl_3"),
    buildInfoPackage := "endtoend.tests.cats"
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `tests-impl` = project
  .in(file("end-to-end-tests/tests-impl"))
  .settings(
    endToEndTestsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    cleanFiles += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe
  )
  .dependsOn(`tests-exports`, `functions-receiver`)

lazy val `tests-http4s-server-impl` = project
  .in(file("end-to-end-tests/tests-http4s-server-impl"))
  .settings(
    endToEndTestsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    cleanFiles += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe ++ Http4sServer
  )
  .dependsOn(`tests-cats-exports`, `http4s-server`)

lazy val `tests-http4s-client-impl` = project
  .in(file("end-to-end-tests/tests-http4s-client-impl"))
  .settings(
    endToEndTestsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    cleanFiles += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe ++ Http4sClient ++ Http4sCirce
  )
  .dependsOn(`tests-cats-exports`, `functions-receiver`, `http4s-client`)

lazy val `using-tests` = project
  .in(file("end-to-end-tests/using-tests"))
  .settings(
    endToEndTestsSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    cleanFiles += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe
  )
  .dependsOn(`tests-exports`, `functions-caller`)

lazy val `using-tests-cats` = project
  .in(file("end-to-end-tests/using-tests-cats"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, CatsEffectsTesting)
  )
  .dependsOn(`tests-http4s-client-impl`, `tests-http4s-server-impl`)
