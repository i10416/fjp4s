import Dependencies._

val scala3Version = "3.1.2"

inThisBuild(
  Seq(
    homepage := Some(url("https://github.com/i10416/fjp4s")),
    organization := "dev.i10416",
    description := "A functional json parser library written in Scala 3 inspired by argonaut.",
    version := "0.1.0-SNAPSHOT"
  )
)

lazy val cross =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Full)
    .in(file("fjp4s"))
    .settings(
      name := "fjp4s",
      // githubOwner := "ItoYo16u",
      // githubRepository := "fjp4s",
      version := version.value,
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
      scalaVersion := scala3Version,
      scalacOptions ++= Seq(
        "-feature",
        "-deprecation"
      ),
      libraryDependencies ++= deps,
      Def.settings(
        Seq(Compile, Test).map { scope =>
          (scope / unmanagedSourceDirectories) += {
            val base = baseDirectory.value.getParentFile / "shared" / "src"
            val dir = base / Defaults.nameForSrc(scope.name)
            dir / "scala3"
          }
        }
      )
    )
