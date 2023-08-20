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
import calico.html.io.{_, given}
import cats.data.Chain
import cats.data.NonEmptyChain
import cats.effect.*
import cats.syntax.all.*
import fs2.concurrent.*
import fs2.dom.*

enum Component[A] {
  case Beside(
      elements: NonEmptyChain[Component[?]],
      styles: Signal[IO, Chain[String]],
      signal: Signal[IO, A]
  )
  case Above(
      elements: NonEmptyChain[Component[?]],
      styles: Signal[IO, Chain[String]],
      signal: Signal[IO, A]
  )
  case Leaf(
      element: HtmlElement[IO],
      styles: Signal[IO, Chain[String]],
      signal: Signal[IO, A]
  )
  case Pure(styles: Signal[IO, Chain[String]], signal: Signal[IO, A])

  def signal: Signal[IO, A]

  /** A time-varying collection of CSS classes that should be applied to this
    * component
    */
  def styles: Signal[IO, Chain[String]]

  def map[B](f: A => B): Component[B] =
    this match {
      case Beside(elements, styles, signal) =>
        Beside(elements, styles, signal.map(f))
      case Above(elements, styles, signal) =>
        Above(elements, styles, signal.map(f))
      case Leaf(element, styles, signal) => Leaf(element, styles, signal.map(f))
      case Pure(styles, signal)          => Pure(styles, signal.map(f))
    }

  def above[B](that: Component[B]): Component[(A, B)] =
    // Optimization to coallesce aboves
    this match {
      case Above(elements1, styles1, signal1) =>
        that match {
          case Above(elements2, styles2, signal2) =>
            Above(
              elements1 ++ elements2,
              (styles1, styles2).mapN((c1, c2) => c1 ++ c2),
              (signal1, signal2).tupled
            )
          case other =>
            Above(elements1 :+ other, styles1, (signal1, other.signal).tupled)
        }

      case _ =>
        that match {
          case Above(elements, styles, signal) =>
            Above(this +: elements, styles, (this.signal, signal).tupled)

          case other =>
            Above(
              NonEmptyChain(this, other),
              Component.emptyStyles,
              (this.signal, other.signal).tupled
            )
        }
    }

  def render: Resource[IO, HtmlElement[IO]] =
    this match {
      case Beside(elements, styles, signal) =>
        div(elements.map(_.render).toList)
      case Above(elements, styles, signal) => div(elements.map(_.render).toList)
      case Leaf(element, styles, signal)   => div(element)
      case Pure(styles, signal)            => div(List.empty[HtmlElement[IO]])
    }

}
object Component {
  def apply[A](element: HtmlElement[IO], signal: Signal[IO, A]): Component[A] =
    Component.Leaf(element, emptyStyles, signal)

  def pure[A](signal: Signal[IO, A]): Component[A] =
    Component.Pure(emptyStyles, signal)

  val emptyStyles: Signal[IO, Chain[String]] =
    Signal.constant[IO, Chain[String]](Chain.empty)
}
