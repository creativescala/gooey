package gooey.calico

import _root_.calico.*
import _root_.calico.html.io.{_, given}
import _root_.calico.syntax.*
import cats.effect.*
import cats.syntax.all.*
import fs2.concurrent.*
import fs2.dom.*
import gooey.component.*

object Algebra
    extends gooey.Algebra,
      Textbox.Algebra,
      Checkbox.Algebra,
      Above.Algebra { self =>

  final case class Component[A](element: HtmlElement[IO], output: Signal[IO, A])

  type UI[A] = Resource[IO, Component[A]]
  def checkbox(c: Checkbox): UI[Boolean] = {
    val Checkbox(label, default) = c
    val element = div(p("checkbox placeholder for $label"))
    val output = SignallingRef[IO].of(default).toResource

    (element, output).mapN((e, o) => Component(e, o))
  }

  def textbox(t: Textbox): UI[String] = {
    val Textbox(label, default, style) = t
    val element = div(p("textbox placeholder for $label"))
    val output = SignallingRef[IO].of(default).toResource

    (element, output).mapN((e, o) => Component(e, o))
  }

  def above[A, B](t: UI[A], b: UI[B]): UI[(A, B)] = {
    for {
      top <- t
      bot <- b
      element = div(top.element, bot.element)
      output = (top.output, bot.output).tupled
      c <- element.map(e => Component(e, output))
    } yield c
  }
}
