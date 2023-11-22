val scala3Version = "3.3.1"

ThisBuild / version := "0.1-SNAPSHOT"

ThisBuild / organization := "org.functions-remote"

name := "functions-remote"

ThisBuild / scalaVersion := scala3Version

ThisBuild / scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-Xmax-inlines", "64")

// ----------------------- dependencies --------------------------------

val Scala3Compiler = "org.scala-lang" %% "scala3-compiler"        % scala3Version
val Scala3Tasty    = "org.scala-lang" %% "scala3-tasty-inspector" % scala3Version
val ScalaTest      = "org.scalatest"  %% "scalatest"              % "3.2.15" % Test

val Diffx              = Seq(
  "com.softwaremill.diffx" %% "diffx-core",
  "com.softwaremill.diffx" %% "diffx-scalatest-should"
).map(_ % "0.7.1" % Test)
val Logback            = "ch.qos.logback"                    % "logback-classic"               % "1.4.6"
val CommonsText        = "org.apache.commons"                % "commons-text"                  % "1.10.0"
val CommonsIO          = "commons-io"                        % "commons-io"                    % "2.11.0"
val Avro4s             = "com.sksamuel.avro4s"              %% "avro4s-core"                   % "5.0.5"
val Mustache           = "com.github.spullara.mustache.java" % "compiler"                      % "0.9.10"
val CirceVersion       = "0.14.1"
val Circe              = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % CirceVersion)
val Http4sVersion      = "0.23.23"
val Http4sServer       = Seq(
  "org.http4s" %% "http4s-ember-server" % Http4sVersion,
  "org.http4s" %% "http4s-dsl"          % Http4sVersion
)
val Http4sClient       = Seq(
  "org.http4s" %% "http4s-ember-client" % Http4sVersion
)
val Http4sCirce        = Seq("org.http4s" %% "http4s-circe" % Http4sVersion)
val CatsEffect         = "org.typelevel"                    %% "cats-effect"                   % "3.5.2"
val CatsEffectsTesting = "org.typelevel"                    %% "cats-effect-testing-scalatest" % "1.5.0" % Test
val KafkaClient        = "org.apache.kafka"                  % "kafka-clients"                 % "3.6.0"
val EmbeddedKafka      = "io.github.embeddedkafka"          %% "embedded-kafka"                % "3.6.0" % Test

val HelidonVersion       = "4.0.0"
val HelidonServer        = "io.helidon.webserver" % "helidon-webserver"       % HelidonVersion
val HelidonClient        = "io.helidon.webclient" % "helidon-webclient-http2" % HelidonVersion
val HelidonServerLogging = "io.helidon.logging"   % "helidon-logging-jul"     % HelidonVersion

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
    libraryDependencies ++= Seq(ScalaTest, Avro4s)
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

lazy val `functions-avro` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(Avro4s)
  )
  .dependsOn(`runtime-and-generator-common`)

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

lazy val `kafka-producer` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(KafkaClient)
  )
  .dependsOn(`functions-caller`)

lazy val `kafka-consumer` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(KafkaClient)
  )
  .dependsOn(`functions-receiver`)

lazy val `loom-sockets-common` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )
  .dependsOn(`functions-common`, fibers)

lazy val `loom-sockets-server` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )
  .dependsOn(`functions-common`, `loom-sockets-common`)

lazy val `loom-sockets-client` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )
  .dependsOn(`functions-common`, `loom-sockets-common`)

lazy val fibers = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest)
  )

lazy val `helidon-server` = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(ScalaTest, HelidonServer)
  )
  .dependsOn(`functions-common`)

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

lazy val `tests-cats-exports` = project
  .in(file("end-to-end-tests/tests-cats-exports"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, CatsEffect),
    buildInfoKeys    := Seq[BuildInfoKey](organization, name, version, scalaVersion, "exportedArtifact" -> "tests-cats-impl_3"),
    buildInfoPackage := "endtoend.tests.cats"
  )
  .enablePlugins(BuildInfoPlugin, FunctionsRemotePlugin)

lazy val `tests-http4s-server-impl` = project
  .in(file("end-to-end-tests/tests-http4s-server-impl"))
  .settings(
    endToEndTestsSettings,
    receiverExports           := Seq(s"functions.end-to-end-tests:tests-cats-exports_3:${version.value}"),
    receiverJsonSerialization := true,
    receiverAvroSerialization := true,
    receiverHttp4sRoutes      := true,
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe ++ Http4sServer
  )
  .dependsOn(`tests-cats-exports`, `http4s-server`, `functions-avro`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-http4s-client-impl` = project
  .in(file("end-to-end-tests/tests-http4s-client-impl"))
  .settings(
    endToEndTestsSettings,
    callerExports               := Seq(s"functions.end-to-end-tests:tests-cats-exports_3:${version.value}"),
    callerAvroSerialization     := true,
    callerJsonSerialization     := true,
    callerHttp4sClientTransport := true,
    libraryDependencies ++= Seq(Avro4s, ScalaTest, CatsEffectsTesting) ++ Circe ++ Http4sClient ++ Http4sCirce
  )
  .dependsOn(`tests-cats-exports`, `functions-receiver`, `http4s-client`, `functions-avro`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-receiver` = project
  .in(file("end-to-end-tests/tests-receiver"))
  .settings(
    endToEndTestsSettings,
    receiverExports           := Seq(s"functions.end-to-end-tests:tests-exports_3:${version.value}"),
    receiverJsonSerialization := true,
    receiverAvroSerialization := true,
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe
  )
  .dependsOn(`tests-exports`, `functions-receiver`, `functions-avro`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-caller` = project
  .in(file("end-to-end-tests/tests-caller"))
  .settings(
    endToEndTestsSettings,
    callerExports                 := Seq(s"functions.end-to-end-tests:tests-exports_3:${version.value}"),
    callerAvroSerialization       := true,
    callerJsonSerialization       := true,
    callerClassloaderTransport    := true,
    callerClassloaderDependencies := Seq(s"functions.end-to-end-tests:tests-receiver_3:${version.value}"),
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe
  )
  .dependsOn(`tests-exports`, `functions-caller`, `functions-avro`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-cats-end-to-end-tests` = project
  .in(file("end-to-end-tests/tests-cats-end-to-end-tests"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, CatsEffectsTesting)
  )
  .dependsOn(`tests-http4s-client-impl`, `tests-http4s-server-impl`)

lazy val `tests-kafka-exports` = project
  .in(file("end-to-end-tests/tests-kafka-exports"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, KafkaClient, Avro4s),
    buildInfoKeys    := Seq[BuildInfoKey](organization, name, version, scalaVersion, "exportedArtifact" -> "tests-kafka-consumer_3"),
    buildInfoPackage := "endtoend.tests.kafka"
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `tests-kafka-producer` = project
  .in(file("end-to-end-tests/tests-kafka-producer"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, KafkaClient, Avro4s) ++ Circe,
    callerExports           := Seq(s"functions.end-to-end-tests:tests-kafka-exports_3:${version.value}"),
    callerAvroSerialization := true,
    callerJsonSerialization := true
  )
  .dependsOn(`tests-kafka-exports`, `kafka-producer`, `functions-avro`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-kafka-consumer` = project
  .in(file("end-to-end-tests/tests-kafka-consumer"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, KafkaClient, Avro4s) ++ Circe,
    receiverExports           := Seq(s"functions.end-to-end-tests:tests-kafka-exports_3:${version.value}"),
    receiverAvroSerialization := true,
    receiverJsonSerialization := true
  )
  .dependsOn(`tests-kafka-exports`, `kafka-consumer`, `functions-avro`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-kafka-end-to-end` = project
  .in(file("end-to-end-tests/tests-kafka-end-to-end"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest, EmbeddedKafka)
  )
  .dependsOn(`tests-kafka-consumer`, `tests-kafka-producer`)

lazy val `tests-loom-sockets` = project
  .in(file("end-to-end-tests/tests-loom-sockets"))
  .settings(
    endToEndTestsSettings,
    callerExports             := Seq(s"functions.end-to-end-tests:tests-exports_3:${version.value}"),
    callerJsonSerialization   := true,
    callerAvroSerialization   := true,
    receiverExports           := Seq(s"functions.end-to-end-tests:tests-exports_3:${version.value}"),
    receiverJsonSerialization := true,
    receiverAvroSerialization := true,
    libraryDependencies ++= Seq(Avro4s, ScalaTest) ++ Circe
  )
  .dependsOn(`loom-sockets-server`, `loom-sockets-client`, `tests-exports`, `tests-receiver`, `functions-receiver`, `functions-avro`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-helidon-exports` = project
  .in(file("end-to-end-tests/tests-helidon-exports"))
  .settings(
    endToEndTestsSettings,
    libraryDependencies ++= Seq(ScalaTest),
    buildInfoKeys    := Seq[BuildInfoKey](organization, name, version, scalaVersion, "exportedArtifact" -> "tests-helidon-server_3"),
    buildInfoPackage := "endtoend.tests.helidon"
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `tests-helidon-server` = project
  .in(file("end-to-end-tests/tests-helidon-server"))
  .settings(
    endToEndTestsSettings,
    receiverExports           := Seq(s"functions.end-to-end-tests:tests-helidon-exports_3:${version.value}"),
    receiverJsonSerialization := true,
    receiverAvroSerialization := true,
    receiverHelidonRoutes     := true,
    libraryDependencies ++= Seq(Avro4s, ScalaTest, HelidonServer, HelidonServerLogging % Test) ++ Circe
  )
  .dependsOn(`functions-receiver`, `functions-avro`, `tests-helidon-exports`, `helidon-server`)
  .enablePlugins(FunctionsRemotePlugin)

lazy val `tests-helidon-client` = project
  .in(file("end-to-end-tests/tests-helidon-client"))
  .settings(
    endToEndTestsSettings,
    callerExports                := Seq(s"functions.end-to-end-tests:tests-helidon-exports_3:${version.value}"),
    callerJsonSerialization      := true,
    callerAvroSerialization      := true,
    callerHelidonClientTransport := true,
    libraryDependencies ++= Seq(Avro4s, ScalaTest, HelidonClient) ++ Circe
  )
  .dependsOn(`functions-caller`, `functions-avro`, `tests-helidon-exports`)
  .enablePlugins(FunctionsRemotePlugin)
