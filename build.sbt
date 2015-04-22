name := "MailClient"

organization := "no.skytteren"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6")

releaseSettings

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

libraryDependencies ++= Seq(
	"javax.mail" % "javax.mail-api" % "1.5.3",
	"com.sun.mail" % "javax.mail" % "1.5.3"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.3"

//should check org.github.rjo1970 dumbster (if it gets published)

libraryDependencies += "com.icegreen" % "greenmail" % "1.3.1b" % "test"


