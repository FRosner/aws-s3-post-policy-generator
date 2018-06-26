lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.6",
  organization := "de.frosner",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
)

lazy val librarySettings = Seq(
  libraryDependencies ++= List(
    "com.github.pureconfig" %% "pureconfig" % "0.9.1",
    "com.github.nscala-time" %% "nscala-time" % "2.20.0"
  ) ++ List(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser").map(_ % "0.9.3")
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(librarySettings: _*)