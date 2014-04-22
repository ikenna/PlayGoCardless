name := "PlayGoCardless"

version := "1.0-SNAPSHOT"

  libraryDependencies ++= Seq(
    jdbc,
    anorm,
    cache,
    "com.gocardless" % "gocardless-java" % "2.0.0",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
  )

play.Project.playScalaSettings


templatesImport += "gocardless.api._"
