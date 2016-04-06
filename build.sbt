lazy val root = (project in file(".")).
  settings(
    name := "vistula",
    version := "1.0",
    scalaVersion := "2.11.7"
  )

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.3.7",
  "org.specs2" %% "specs2-core" % "3.7.2" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
