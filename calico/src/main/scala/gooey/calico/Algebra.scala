package gooey.calico

import _root_.calico.html.io.{*, given}
import cats.effect.*
import fs2.dom.*
import gooey.component.*

object Algebra
    extends gooey.Algebra,
      Textbox.Algebra,
      Checkbox.Algebra,
      Above.Algebra { self =>
  type UI[A] = Resource[IO, HtmlElement[IO]]
  def checkbox(c: Checkbox): UI[Boolean] = {
    val Checkbox(label, default) = c
    div(p("checkbox placeholder for $label"))
  }

  def textbox(t: Textbox): UI[String] = {
    val Textbox(label, default, style) = t
    div(p("textbox placeholder for $label"))
  }

  def above[A, B](t: UI[A], b: UI[B]): UI[(A, B)] = {
    div(t, b)
  }
}
