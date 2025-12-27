val scala3Version = "3.7.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Scala 3 Project Template",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.51.1.0",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "4.3.5",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "4.4.1",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.16",
    libraryDependencies += "io.javalin" % "javalin" % "6.7.0",
  )
