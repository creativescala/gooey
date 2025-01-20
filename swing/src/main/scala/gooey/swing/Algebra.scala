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

package gooey.swing

import cats.data.Chain
import cats.effect.IO
import cats.effect.Resource
import cats.syntax.all.*
import fs2.concurrent.*
import gooey.WritableVar
import gooey.component.And
import gooey.component.Checkbox
import gooey.component.Map
import gooey.component.Pure
import gooey.component.Textbox
import gooey.component.style.*
import net.bulbyvr.swing.io.all.{*, given}
import net.bulbyvr.swing.io.wrapper.*

type Algebra =
  gooey.Algebra & And.Algebra & Checkbox.Algebra & Map.Algebra & Pure.Algebra &
    Textbox.Algebra

given Algebra: gooey.Algebra
  with And.Algebra
  with Checkbox.Algebra
  with Map.Algebra
  with Pure.Algebra
  with Textbox.Algebra
  with {

  type Env = Environment

  type UI[A] = Resource[IO, (Component[IO], Signal[IO, A])]

  def initialize(): Env = Environment.empty

  def and[A, B](f: UI[A], s: UI[B])(env: Env): UI[(A, B)] = {
    for {
      fst <- f
      snd <- s
      (c1, s1) = fst
      (c2, s2) = snd
      c <- box(c1, c2)
    } yield (c, (s1, s2).tupled)
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
      val component =
        makeComponent(
          makeLabel(label),
          net.bulbyvr.swing.io.all.checkbox.withSelf { self =>
            onBtnClick --> {
              _.evalMap(_ => self.selected.get).foreach(output.set)
            }
          }
        )
      signals *> component.map(c => (c, output))
    }
  }

  def map[A, B](source: UI[A], f: A => B)(env: Env): UI[B] =
    source.map { case (c, s) => (c, s.map(f)) }

  def pure[A](value: A)(env: Env): UI[A] = {
    ???
  }

  def textbox(
      label: Option[String],
      default: String,
      style: TextboxStyle,
      observers: Chain[WritableVar[String]]
  )(env: Env): UI[String] = {
    SignallingRef[IO].of(default).toResource.flatMap { output =>
      val signals = addSources(observers, output, env)
      val component =
        makeComponent(
          makeLabel(label),
          style match {
            case TextboxStyle.SingleLine =>
              textField.withSelf { self =>
                onValueChange --> {
                  _.foreach(_ => self.text.get.flatMap(output.set))
                }
              }

            case TextboxStyle.MultiLine =>
              ???
          }
        )
      signals *> component.map(c => (c, output))
    }
  }

  // Utilities -------------------------------------------------------

  def makeComponent[A](
      label: Resource[IO, Component[IO]],
      element: Resource[IO, Component[IO]]
  ): Resource[IO, Component[IO]] = {
    box(label, element)
  }

  def makeLabel(theLabel: Option[String]): Resource[IO, Component[IO]] =
    theLabel.fold(label(text := "")) { l => label(text := l) }

  def addSources[A](
      observers: Chain[WritableVar[A]],
      source: Signal[IO, A],
      env: Environment
  ): Resource[IO, Chain[Signal[IO, A]]] =
    observers.traverse(o => env.addSource(o.id, source)).toResource
}
