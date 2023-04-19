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
import _root_.calico.html.io.{_, given}
import _root_.calico.syntax.*
import cats.effect.*
import cats.syntax.all.*
import fs2.concurrent.*
import fs2.dom.*
import gooey.component.And
import gooey.component.Checkbox
import gooey.component.Map
import gooey.component.Pure
import gooey.component.Textbox
import gooey.component.style.*

given Algebra: gooey.Algebra
  with And.Algebra
  with Checkbox.Algebra
  with Map.Algebra
  with Pure.Algebra
  with Textbox.Algebra
  with {

  type UI[A] = gooey.calico.UI[A]

  val checkboxClass =
    "border rounded text-gray-700 focus:outline-none focus:shadow-outline"

  val elementClass =
    "appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"

  def makeComponent(
      label: Resource[IO, HtmlElement[IO]],
      element: Resource[IO, HtmlElement[IO]]
  ): Resource[IO, HtmlElement[IO]] =
    div(cls := "mb-4", label, element)

  def makeLabel(theLabel: Option[String]): Resource[IO, HtmlElement[IO]] =
    theLabel.fold(span(())) { l =>
      label(cls := "block text-gray-700 text-sm font-bold font-sans mb-2", l)
    }

  def and[A, B](f: UI[A], s: UI[B]): UI[(A, B)] = {
    import calico.frp.given
    for {
      fst <- f
      snd <- s
    } yield fst.product(snd)
  }

  def checkbox(label: Option[String], default: Boolean): UI[Boolean] = {
    SignallingRef[IO].of(default).toResource.flatMap { output =>
      val element =
        makeComponent(
          makeLabel(label),
          input.withSelf { self =>
            (
              `type` := "checkbox",
              cls := checkboxClass,
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
        makeComponent(
          makeLabel(label),
          style match {
            case TextboxStyle.SingleLine =>
              input.withSelf { self =>
                (
                  value := default,
                  `type` := "text",
                  cls := elementClass,
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
