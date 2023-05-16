import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  // Library Versions
  val calicoVersion = "0.2.0"
  val catsVersion = "2.9.0"
  val catsEffectVersion = "3.5.0"
  val fs2Version = "3.6.1"
  val scalaJsDomVersion = "2.4.0"
  val swingIoVersion = "0.1.0-M1"

  val munitVersion = "0.7.29"

  // Libraries
  val calico = Def.setting("com.armanbilge" %%% "calico" % calicoVersion)
  val catsEffect =
    Def.setting("org.typelevel" %%% "cats-effect" % catsEffectVersion)
  val catsCore = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
  val fs2Core = Def.setting("co.fs2" %% "fs2-core" % fs2Version)
  val scalaJsDom =
    Def.setting("org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion)
  val swingIo = Def.setting(
    "io.github.thedrawingcoder-gamer" %%% "swing-io" % swingIoVersion
  )

  val munit = Def.setting("org.scalameta" %% "munit" % munitVersion % "test")
}
