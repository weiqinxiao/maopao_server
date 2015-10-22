name := "playTest"

version := "1.0"

lazy val `playtest` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws, javaEbean,
                            "mysql" % "mysql-connector-java" % "5.1.35",
                            "com.qiniu" % "qiniu-java-sdk" % "7.0.+",
                            "me.chanjar" % "weixin-java-mp" % "1.3.1")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  