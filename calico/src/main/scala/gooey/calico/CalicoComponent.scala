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
import cats.data.NonEmptyChain
import cats.effect.*
import cats.syntax.all.*
import fs2.concurrent.*
import fs2.dom.*

enum CalicoComponent[A] {
  case Beside(
      elements: NonEmptyChain[CalicoComponent[?]],
      classes: Signal[IO, List[String]],
      signal: Signal[IO, A]
  )
  case Above(
      elements: NonEmptyChain[CalicoComponent[?]],
      classes: Signal[IO, List[String]],
      signal: Signal[IO, A]
  )
  case Leaf(
      element: HtmlElement[IO],
      classes: Signal[IO, List[String]],
      signal: Signal[IO, A]
  )
  case Pure(classes: Signal[IO, List[String]], signal: Signal[IO, A])

  /** The time-varying output value of this component */
  def signal: Signal[IO, A]

  /** A time-varying collection of CSS classes that should be applied to this
    * component
    */
  def classes: Signal[IO, List[String]]

  def addClass(cls: Signal[IO, String]): CalicoComponent[A] = {
    // TODO: Is this sequential or parallel? We want parallel.
    val newClasses = (classes, cls).mapN((accum, elt) => elt :: accum)

    this match {
      case Beside(elements, _, signal) =>
        Beside(elements, newClasses, signal)
      case Above(elements, _, signal) =>
        Above(elements, newClasses, signal)
      case Leaf(element, _, signal) =>
        Leaf(element, newClasses, signal)
      case Pure(_, signal) =>
        Pure(newClasses, signal)
    }
  }

  def map[B](f: A => B): CalicoComponent[B] =
    this match {
      case Beside(elements, classes, signal) =>
        Beside(elements, classes, signal.map(f))
      case Above(elements, classes, signal) =>
        Above(elements, classes, signal.map(f))
      case Leaf(element, classes, signal) =>
        Leaf(element, classes, signal.map(f))
      case Pure(classes, signal) => Pure(classes, signal.map(f))
    }

  def above[B](that: CalicoComponent[B]): CalicoComponent[(A, B)] =
    // Optimization to coallesce aboves
    this match {
      case Above(elements1, classes1, signal1) =>
        that match {
          case Above(elements2, classes2, signal2) =>
            Above(
              elements1 ++ elements2,
              (classes1, classes2).mapN((c1, c2) => c1 ++ c2),
              (signal1, signal2).tupled
            )
          case other =>
            Above(elements1 :+ other, classes1, (signal1, other.signal).tupled)
        }

      case _ =>
        that match {
          case Above(elements, classes, signal) =>
            Above(this +: elements, classes, (this.signal, signal).tupled)

          case other =>
            Above(
              NonEmptyChain(this, other),
              CalicoComponent.emptyClasses,
              (this.signal, other.signal).tupled
            )
        }
    }

  def render: Resource[IO, HtmlElement[IO]] =
    this match {
      case Beside(elements, classes, signal) =>
        div(cls <-- classes, elements.map(_.render).toList)
      case Above(elements, classes, signal) =>
        div(cls <-- classes, elements.map(_.render).toList)
      case Leaf(element, classes, signal) =>
        div(cls <-- classes, element)
      case Pure(classes, signal) =>
        div(cls <-- classes, List.empty[HtmlElement[IO]])
    }

}
object CalicoComponent {
  def apply[A](
      element: HtmlElement[IO],
      signal: Signal[IO, A]
  ): CalicoComponent[A] =
    CalicoComponent.Leaf(element, emptyClasses, signal)

  def apply[A](
      element: HtmlElement[IO],
      classes: Signal[IO, List[String]],
      signal: Signal[IO, A]
  ): CalicoComponent[A] =
    CalicoComponent.Leaf(element, classes, signal)

  def pure[A](signal: Signal[IO, A]): CalicoComponent[A] =
    CalicoComponent.Pure(emptyClasses, signal)

  val emptyClasses: Signal[IO, List[String]] =
    Signal.constant[IO, List[String]](List.empty)
}
