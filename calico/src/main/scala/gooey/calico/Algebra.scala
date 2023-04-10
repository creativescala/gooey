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
    val Checkbox(theLabel, default) = c
    val output = SignallingRef[IO].of(default).toResource
    val element = output.flatMap { output =>
      div(
        theLabel.fold(span(()))(l => label(l)),
        input.withSelf { self =>
          (
            `type` := "checkbox",
            onInput --> (_.foreach(_ =>
              self.value.get.flatMap(v => output.set(v == "on"))
            ))
          )
        }
      )
    }

    (element, output).mapN((e, o) => Component(e, o))
  }

  def textbox(t: Textbox): UI[String] = {
    val Textbox(theLabel, default, style) = t
    val output = SignallingRef[IO].of(default).toResource
    val element = output.flatMap { output =>
      div(
        theLabel.fold(span(()))(l => label(l)),
        input.withSelf { self =>
          (
            value := default,
            `type` := "text",
            onInput --> (_.foreach(_ => self.value.get.flatMap(output.set)))
          )
        }
      )
    }

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
