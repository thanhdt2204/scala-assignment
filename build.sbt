
ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

updateOptions := updateOptions.value.withCachedResolution(true)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "APP",
    libraryDependencies ++= (appDependencies ++ testDependencies).map(excludeBadTransitiveDependencies),
  )

// Separated project for running integration test
lazy val integration = (project in file("integrationTest"))
  .dependsOn(root, root % "test->test")
  .settings(
    name := "IT",
    libraryDependencies ++= testDependencies.map(excludeBadTransitiveDependencies),
    sourceDirectory := baseDirectory.value,
    Test / testOptions ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-o"),
      Tests.Argument(TestFrameworks.ScalaTest, "-h", s"${target.value}/test-html")
    ),
    Test / test := (Test / test).dependsOn(root / Assets / packageBin).value,
    Test / fork := false,
    Test / parallelExecution := false,
    Test / managedClasspath += (root / Assets / packageBin).value,
  )

// Dependencies
val playSilhouetteVersion = "6.1.1"
val slickVersion = "3.3.3"
val playSlickVersion = "5.0.0"
val akkaVersion = "2.6.21"
val enumeratumVersion = "1.7.0"
val enumeratumSlickVersion = "1.5.16"

val appDependencies = Seq(
  guice,
  ws,
  filters,

  // DB
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,
  "org.postgresql" % "postgresql" % "42.2.10",

  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.15",

  "org.joda" % "joda-convert" % "2.2.3",

  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",

  "net.logstash.logback" % "logstash-logback-encoder" % "6.3",
  "net.codingwell" %% "scala-guice" % "4.2.11",
  "io.lemonlabs" %% "scala-uri" % "4.0.3",

  // JWT
  "com.mohiva" %% "play-silhouette" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-crypto-jca" % playSilhouetteVersion,

  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "com.beachape" %% "enumeratum-play" % enumeratumVersion,
  "com.beachape" %% "enumeratum-play-json" % enumeratumVersion,
  "com.beachape" %% "enumeratum-slick" % enumeratumSlickVersion,

)

val testDependencies = Seq(
  specs2,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.opentable.components" % "otj-pg-embedded" % "0.13.4",
  "org.mockito" % "mockito-core" % "4.0.0",
  "org.mockito" % "mockito-scala_2.13" % "1.17.14",
  "com.mohiva" %% "play-silhouette-testkit" % playSilhouetteVersion,
  "org.pegdown" % "pegdown" % "1.5.0"
).map(_ % Test)

def excludeBadTransitiveDependencies(mod: ModuleID): ModuleID = mod.excludeAll(
  ExclusionRule(organization = "commons-logging"),
  ExclusionRule(organization = "org.slf4j", name = "slf4j-log4j12")
)

resolvers += Resolver.jcenterRepo
resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"