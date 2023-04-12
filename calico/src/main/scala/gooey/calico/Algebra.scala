/*
 * Copyright 2023 Creative Scala
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

package gooey.calico

import _root_.calico.*
import _root_.calico.html.io.{*, given}
import _root_.calico.syntax.*
import cats.effect.*
import cats.syntax.all.*
import fs2.concurrent.*
import fs2.dom.*
import gooey.component.*
import gooey.component.style.*

type UI[A] = Resource[IO, Component[A]]

final case class Component[A](element: HtmlElement[IO], output: Signal[IO, A]) {
  def map[B](f: A => B): Component[B] =
    this.copy(output = output.map(f))
}

given Algebra: gooey.Algebra
  with Above.Algebra
  with Checkbox.Algebra
  with Map.Algebra
  with Pure.Algebra
  with Textbox.Algebra
  with {

  type UI[A] = gooey.calico.UI[A]

  def makeLabel(theLabel: Option[String]): Resource[IO, HtmlElement[IO]] =
    theLabel.fold(span(()))(l => label(l))

  def above[A, B](t: UI[A], b: UI[B]): UI[(A, B)] = {
    import calico.frp.given
    for {
      top <- t
      bot <- b
      element <- div(top.element, bot.element)
      output = top.output.flatMap(t => bot.output.map(b => (t, b)))
    } yield Component(element, output)
  }

  def checkbox(label: Option[String], default: Boolean): UI[Boolean] = {
    SignallingRef[IO].of(default).toResource.flatMap { output =>
      val element =
        div(
          makeLabel(label),
          input.withSelf { self =>
            (
              `type` := "checkbox",
              checked := default,
              onChange --> (_.foreach { _ =>
                output.getAndUpdate(v => !v).void
              })
            )
          }
        )
      element.map(e => Component(e, output))
    }
  }

  def map[A, B](source: UI[A], f: A => B): UI[B] =
    source.map(component => component.map(f))

  def pure[A](value: A): UI[A] =
    span(()).map(elt => Component(elt, Signal.constant[IO, A](value)))

  def textbox(
      label: Option[String],
      default: String,
      style: TextboxStyle
  ): UI[String] = {
    SignallingRef[IO].of(default).toResource.flatMap { output =>
      val element =
        div(
          makeLabel(label),
          style match {
            case TextboxStyle.SingleLine =>
              input.withSelf { self =>
                (
                  value := default,
                  `type` := "text",
                  onInput --> (_.foreach(_ =>
                    self.value.get.flatMap(output.set)
                  ))
                )
              }
            case TextboxStyle.MultiLine =>
              textArea.withSelf { self =>
                (
                  value := default,
                  onInput --> (_.foreach(_ =>
                    self.value.get.flatMap(output.set)
                  ))
                )
              }
          }
        )
      element.map(e => Component(e, output))
    }
  }

}