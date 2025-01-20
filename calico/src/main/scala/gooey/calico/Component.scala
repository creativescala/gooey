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

import calico.frp.given
import calico.html.io.{*, given}
import cats.data.Chain
import cats.effect.*
import cats.syntax.all.*
import fs2.concurrent.*
import fs2.dom.*

final case class Component[A](
    elements: Chain[HtmlElement[IO]],
    signal: Signal[IO, A]
) {
  def map[B](f: A => B): Component[B] =
    this.copy(signal = signal.map(f))

  def product[B](that: Component[B]): Component[(A, B)] =
    Component(
      this.elements ++ that.elements,
      (this.signal, that.signal).tupled
    )

  def buildElement: Resource[IO, HtmlElement[IO]] =
    build.map((e, _) => e)

  def build: Resource[IO, (HtmlElement[IO], Signal[IO, A])] =
    div(elements.toList).map(elt => (elt, signal))
}
object Component {
  def apply[A](
      element: HtmlElement[IO],
      signal: Signal[IO, A]
  ): Component[A] =
    Component(Chain(element), signal)
}
