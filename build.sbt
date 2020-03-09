name := "ip4m_message_fetch"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.23"

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2"

libraryDependencies += "joda-time" % "joda-time" % "2.9.9"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.6"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.6"
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

mainClass in assembly := Some("Main")
