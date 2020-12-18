scalaVersion := "2.13.4"

name := "rpiled-srv"
organization := "com.ruimo"

lazy val root = (project in file(".")).enablePlugins(UniversalDeployPlugin, JavaAppPackaging)

libraryDependencies += "com.pi4j" % "pi4j-core" % "1.2"
libraryDependencies += "com.pi4j" % "pi4j-gpio-extension" %"1.2"
libraryDependencies += "org.specs2" %% "specs2-core" % "4.10.2" % "test"

publishTo := Some(
  Resolver.file(
    "rpiled-srv",
    new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
  )
)
