name := "MailClient"

organization := "no.skytteren"

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.10.4", "2.11.1")

releaseSettings

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

libraryDependencies += "javax.mail" % "mail" % "1.4.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"

libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.2"

//should check org.github.rjo1970 dumbster (if it gets published)

libraryDependencies += "com.icegreen" % "greenmail" % "1.3.1b" % "test"


