val scala3Version = "3.3.1"

ThisBuild / version := "0.1-SNAPSHOT"

ThisBuild / organization := "org.kkougios"

name := "scala-codegen2"

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

val Avro4s   = "com.sksamuel.avro4s"              %% "avro4s-core" % "5.0.4"
val Mustache = "com.github.spullara.mustache.java" % "compiler"    % "0.9.10"

val commonSettings = Seq(
)

lazy val `tasty-extractor` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(Scala3Tasty, ScalaTest, CommonsIO, CommonsText) ++ Diffx
  )

lazy val `proxy-generator` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest, CommonsIO, CommonsText) ++ Diffx
  )
  .dependsOn(`templates-lib`, `proxy-templates`, `tasty-extractor`)
  .enablePlugins(PackPlugin)

lazy val `templates-lib` = project.settings(
  commonSettings,
  libraryDependencies ++= Seq(ScalaTest, CommonsIO, CommonsText, Mustache) ++ Diffx
)

lazy val `proxy-templates` = project.settings(
  libraryDependencies ++= Seq(Avro4s)
)

lazy val `functions-common` = project.settings(commonSettings)

lazy val `functions-discovery` = project.settings(commonSettings).dependsOn(`functions-common`)

lazy val `functions-receiver` = project.settings(commonSettings).dependsOn(`functions-common`)

lazy val `ls-exports` = project
  .in(file("example-commands/ls-exports"))
  .settings(
    commonSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated",
    libraryDependencies ++= Seq(Avro4s, ScalaTest)
  )
  .enablePlugins(PackPlugin)

lazy val ls = project
  .in(file("example-commands/ls"))
  .settings(
    commonSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated"
  )
  .dependsOn(`ls-exports`, `functions-receiver`)
  .enablePlugins(PackPlugin)

lazy val `using-commands` = project
  .in(file("example-commands/using-commands"))
  .settings(
    commonSettings,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "generated"
  )
  .dependsOn(`ls-exports`, `functions-discovery`)
