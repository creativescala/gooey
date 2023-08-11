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
import cats.data.Chain
import cats.data.NonEmptySeq
import cats.effect.*
import cats.syntax.all.*
import fs2.concurrent.*
import fs2.dom.*
import gooey.Display
import gooey.Var
import gooey.WritableVar
import gooey.component.And
import gooey.component.Checkbox
import gooey.component.Dropdown
import gooey.component.Form
import gooey.component.Map
import gooey.component.Observe
import gooey.component.Pure
import gooey.component.Slider
import gooey.component.Text
import gooey.component.Textbox
import gooey.component.style.*

type Algebra =
  gooey.Algebra & And.Algebra & Checkbox.Algebra & Dropdown.Algebra &
    Form.Algebra & Map.Algebra & Observe.Algebra & Pure.Algebra &
    Slider.Algebra & Text.Algebra & Textbox.Algebra

given Algebra: gooey.Algebra
  with And.Algebra
  with Checkbox.Algebra
  with Dropdown.Algebra
  with Form.Algebra
  with Map.Algebra
  with Observe.Algebra
  with Pure.Algebra
  with Slider.Algebra
  with Text.Algebra
  with Textbox.Algebra
  with {

  type Env = Environment

  type UI[A] = gooey.calico.UI[A]

  def initialize(): Env = Environment.empty

  def and[A, B](f: UI[A], s: UI[B])(env: Env): UI[(A, B)] = {
    for {
      fst <- f
      snd <- s
    } yield fst.product(snd)
  }

  def checkbox(
      label: Option[String],
      default: Boolean,
      observers: Chain[WritableVar[Boolean]]
  )(
      env: Env
  ): UI[Boolean] = {
    SignallingRef[IO].of(default).toResource.flatMap { output =>
      val signals = addSources(observers, output, env)
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
      signals *> element.map(e => Component(e, output))
    }
  }

  def dropdown[A](
      label: Option[String],
      choices: NonEmptySeq[(String, A)],
      observers: Chain[WritableVar[A]]
  )(env: Env): UI[A] = {
    val (_, a) = choices.head
    SignallingRef[IO].of(a).toResource.flatMap { output =>
      val signals = addSources(observers, output, env)
      val element =
        makeComponent(
          makeLabel(label),
          select.withSelf { self =>
            (
              cls := elementClass,
              choices.map((name, _) => option(name)).toList,
              onChange --> (_.foreach { _ =>
                self.value.get
                  .flatMap(choice =>
                    choices
                      .find((c, _) => c == choice)
                      .fold(output.set(a))((_, a) => output.set(a))
                  )
              })
            )
          }
        )
      signals *> element.map(e => Component(e, output))
    }
  }

  def form[A](
      title: String,
      component: UI[A],
      submit: String,
      onSubmit: A => Unit
  )(env: Env): UI[A] = {
    component.flatMap { c =>
      val elts = c.elements
      val signal = c.signal

      calico.html.io
        .form(
          h2(title),
          elts.toList,
          button(
            `type` := "button",
            onClick --> (_.foreach { _ => signal.get.map(onSubmit) }),
            submit
          )
        )
        .map(elements => Component(elements, signal))
    }
  }

  def map[A, B](source: UI[A], f: A => B)(env: Env): UI[B] =
    source.map(component => component.map(f))

  def observe[A](source: UI[A], observer: WritableVar[A])(env: Env): UI[A] =
    IO.println("observe").toResource *> source.evalMap(component =>
      env.addSource(observer.id, component.signal).as(component)
    )

  def pure[A](value: A)(env: Env): UI[A] =
    Resource.eval(IO(Component(Chain.empty, Signal.constant[IO, A](value))))

  def slider(
      label: Option[String],
      min: Int,
      max: Int,
      default: Int,
      observers: Chain[WritableVar[Int]]
  )(env: Env): UI[Int] = {
    SignallingRef[IO].of(default).toResource.flatMap { output =>
      val signals = addSources(observers, output, env)
      val element =
        makeComponent(
          makeLabel(label),
          input.withSelf { self =>
            (
              value := default.toString,
              `type` := "range",
              minAttr := min.toString,
              maxAttr := max.toString,
              onChange --> (_.foreach(_ =>
                self.value.get.map(_.toInt).flatMap(output.set)
              ))
            )
          }
        )
      signals *> element.map(e => Component(e, output))
    }
  }

  def text(content: Var[String], display: Var[Display])(
      env: Environment
  ): UI[Unit] =
    (env.getOrCreate(content), env.getOrCreate(display)).tupled.toResource
      .flatMap { (content, display) =>
        p(
          calico.html.io.styleAttr <-- display.map {
            case Display.Show => ""
            case Display.Hide => "display: none;"
          },
          content
        ).map(elt => Component(elt, Signal.constant[IO, Unit](())))
      }

  def textbox(
      label: Option[String],
      default: String,
      style: TextboxStyle,
      observers: Chain[WritableVar[String]],
      display: Var[gooey.Display]
  )(env: Env): UI[String] = {
    SignallingRef[IO].of(default).toResource.flatMap { output =>
      val signals = addSources(observers, output, env)
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
      signals *> element.map(e => Component(e, output))
    }
  }

  // Utilities -------------------------------------------------------

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

  def addSources[A](
      observers: Chain[WritableVar[A]],
      source: Signal[IO, A],
      env: Environment
  ): Resource[IO, Chain[Signal[IO, A]]] =
    observers.traverse(o => env.addSource(o.id, source)).toResource
}
