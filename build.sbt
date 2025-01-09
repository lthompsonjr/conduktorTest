ThisBuild / scalaVersion     := "2.13.15"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "conduktorTest",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.1.14",
      "dev.zio" %% "zio-test" % "2.1.14" % Test,
      "dev.zio" %% "zio-macros" % "2.1.14",
      "org.apache.kafka" % "kafka-clients" % "3.9.0",
      "dev.zio" %% "zio-json" % "0.6.2",
      "io.scalaland" %% "chimney" % "1.6.0",
      "dev.zio" %% "zio-http" % "3.0.1"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

scalacOptions += "-Ymacro-annotations"