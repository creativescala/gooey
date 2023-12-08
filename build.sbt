/*
 * Copyright 2015-2020 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import scala.sys.process._
import laika.rewrite.link.LinkConfig
import laika.rewrite.link.ApiLinks
import laika.theme.Theme

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / tlBaseVersion := "0.1" // your current series x.y

ThisBuild / organization := "org.creativescala"
ThisBuild / organizationName := "Creative Scala"
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("noelwelsh", "Noel Welsh")
)

// true by default, set to false to publish to s01.oss.sonatype.org
ThisBuild / tlSonatypeUseLegacyHost := true

lazy val scala3 = "3.2.2"

ThisBuild / crossScalaVersions := List(scala3)
ThisBuild / scalaVersion := crossScalaVersions.value.head
ThisBuild / useSuperShell := false
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / tlSitePublishBranch := Some("main")

// Run this (build) to do everything involved in building the project
commands += Command.command("build") { state =>
  "dependencyUpdates" ::
    "compile" ::
    "test" ::
    "docs / tlSite" ::
    "scalafixAll" ::
    "scalafmtAll" ::
    "headerCreateAll" ::
    state
}

lazy val css = taskKey[Unit]("Build the CSS")

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    Dependencies.munit.value
  )
)

lazy val root = project
  .in(file("."))
  .settings(moduleName := "gooey")
  .aggregate(
    core.js,
    core.jvm,
    generic.js,
    generic.jvm,
    calico,
    swing,
    examples.js,
    examples.jvm
  )

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .in(file("core"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      Dependencies.catsCore.value,
      Dependencies.catsEffect.value
    ),
    moduleName := "gooey-core"
  )

lazy val generic = crossProject(JSPlatform, JVMPlatform)
  .in(file("generic"))
  .settings(
    commonSettings,
    libraryDependencies += Dependencies.magnolia.value,
    moduleName := "gooey-generic"
  )

lazy val calico = project
  .in(file("calico"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(Dependencies.calico.value),
    moduleName := "gooey-calico"
  )
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(core.js)

lazy val swing = project
  .in(file("swing"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(Dependencies.swingIo.value),
    moduleName := "gooey-swing"
  )
  .dependsOn(core.jvm)

lazy val docs =
  project
    .in(file("docs"))
    .settings(
      laikaConfig := laikaConfig.value.withConfigValue(
        LinkConfig(apiLinks =
          Seq(
            ApiLinks(baseUri =
              "https://javadoc.io/doc/org.creativescala/gooey-docs_3/latest/"
            )
          )
        )
      ),
      mdocIn := file("docs/src/pages"),
      mdocVariables := {
        mdocVariables.value ++ Map(
          "CALICO_VERSION" -> Dependencies.calicoVersion
        )
      },
      css := {
        val src = file("docs/src/css")
        val dest1 = mdocOut.value
        val dest2 = (laikaSite / target).value
        val cmd1 =
          s"npx tailwindcss -i ${src.toString}/creative-scala.css -o ${dest1.toString}/creative-scala.css"
        val cmd2 =
          s"npx tailwindcss -i ${src.toString}/creative-scala.css -o ${dest2.toString}/creative-scala.css"
        cmd1 !

        cmd2 !
      },
      Laika / sourceDirectories ++=
        Seq(
          file("docs/src/templates"),
          (examples.js / Compile / fastOptJS / artifactPath).value
            .getParentFile() / s"${(examples.js / moduleName).value}-fastopt"
        ),
      laikaTheme := Theme.empty,
      laikaExtensions ++= Seq(
        laika.markdown.github.GitHubFlavor,
        laika.parse.code.SyntaxHighlighting,
        CreativeScalaDirectives
      ),
      tlSite := Def
        .sequential(
          (examples.js / Compile / fastLinkJS),
          mdoc.toTask(""),
          css,
          laikaSite
        )
        .value
    )
    .enablePlugins(TypelevelSitePlugin)
    .dependsOn(core.jvm)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin) // also enables the ScalaUnidocPlugin
  .settings(
    name := "gooey-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter :=
      inAnyProject -- inProjects(
        docs,
        examples.js,
        core.js
      )
  )

// To avoid including this in the core build
lazy val examples = crossProject(JSPlatform, JVMPlatform)
  .in(file("examples"))
  .settings(
    commonSettings,
    moduleName := "gooey-examples"
  )
  .jvmConfigure(
    _.settings(mimaPreviousArtifacts := Set.empty)
      .dependsOn(core.jvm, swing)
  )
  .jsConfigure(
    _.settings(mimaPreviousArtifacts := Set.empty)
      .dependsOn(core.js, calico)
  )
