package gooey.examples

import calico.*
import calico.html.io.{_, given}
import calico.syntax.*
import calico.unsafe.given
import cats.effect.*
import fs2.dom.*
import gooey.calico.*
import gooey.component.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("BasicCalico")
object BasicCalico {
  @JSExport
  def mount(id: String): Unit = {
    val rootElement =
      Window[IO].document.getElementById(id).map(_.get)
    rootElement.flatMap(render.renderInto(_).useForever).unsafeRunAndForget()
  }

  def render: Resource[IO, HtmlElement[IO]] =
    Above(
      Checkbox.empty.withLabel("Is this awesome?"),
      Textbox.empty.withLabel(
        "Describe, in your own words, the amount of awesomeness"
      )
    )(Algebra).map(_.element)
}
