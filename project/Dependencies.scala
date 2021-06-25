import sbt._

object Dependencies {
  val scalaTestVersion = "3.2.9"

  val deps = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  )
}
