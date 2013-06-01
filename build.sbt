name := "MailClient"

organization := "no.skytteren"

scalaVersion := "2.10.1"

libraryDependencies += "javax.mail" % "mail" % "1.4.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0.M6-SNAP19" % "test"

//should check org.github.rjo1970 dumbster (if it gets published)

libraryDependencies += "com.icegreen" % "greenmail" % "1.3.1b" % "test"

