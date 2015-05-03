import com.github.play2war.plugin._

name := "playTest"

version := "1.0"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "2.5"

lazy val `playtest` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws, javaEbean,
                            "mysql" % "mysql-connector-java" % "5.1.35")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

