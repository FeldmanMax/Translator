name := "Translator"
 
version := "1.0" 
      
lazy val `translator` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

resolvers ++= Seq(
  "tpolecat" at "http://dl.bintray.com/tpolecat/maven",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

scalaVersion := "2.12.2"

scalacOptions += "-Ypartial-unification"

val circeVersion = "0.9.3"

val circeDependencies = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val testDependencies = Seq(
  "org.scalatest"          %% "scalatest"          % "3.0.5" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % "test"
)

lazy val doobieVersion = "0.5.3"
val databaseDependencies = Seq (
  "org.tpolecat"  %% "doobie-core"           % doobieVersion,
  "org.tpolecat"  %% "doobie-postgres"       % doobieVersion,
  "org.postgis"   % "postgis-jdbc"           % "1.3.3"
)

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice,
  "org.scalactic"               %% "scalactic"              % "3.0.5",
  "org.slf4j"                   %  "slf4j-nop"              % "1.6.4",
  "com.typesafe"                %  "config"                 % "1.3.2",
  "org.scalaz.stream"           %% "scalaz-stream"          % "0.8.6",
  "org.typelevel"               %% "cats-effect"            % "1.0.0"
) ++ circeDependencies ++ testDependencies ++ databaseDependencies

PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value

javaOptions in Test += s"-Dconfig.file=conf/application.test.conf"
fork in test := true