import Dependencies._
val scala3Version = "3.0.0"
val scala2Version = "2.13.6"

ThisBuild / homepage := Some(url("https://github.com/ItoYo16u/fjp4s"))
ThisBuild / description := "A functional json parser library written in Scala 3 inspired by argonaut."
ThisBuild / version := "0.1.0"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val root = project
  .in(file("fjp4s/shared"))
  .settings(
    name := "fjp4s",
    version := version.value,
    scalaVersion := scala3Version,
    scalacOptions ++= Seq("-Ywarn-unused-import", "-Ywarn-adapted-args"),
    libraryDependencies ++= deps,
    crossScalaVersions := Seq(scala3Version, scala2Version),
    Def.settings(
      Seq(Compile,Test).map {scope =>
        (scope / unmanagedSourceDirectories) += {
          val base = baseDirectory.value.getParentFile / "shared" / "src"
          val dir = base / Defaults.nameForSrc(scope.name)
          dir / "scala3"
        }
      }
    )
  )
