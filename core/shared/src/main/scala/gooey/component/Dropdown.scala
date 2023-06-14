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

package gooey.component

import cats.data.Chain
import gooey.WritableVar

/** A dropdown list for selecting a single element from a collection. Each
  * element is a pair of a String and a value of type A. The String is the label
  * for the element, and the value of type A is the value that is returned if
  * that label is chosen.
  */
final case class Dropdown[A](
    label: Option[String],
    choices: Iterable[(String, A)],
    observers: Chain[WritableVar[A]]
) extends Component[Dropdown.Algebra, Option[A]],
      Labelable[Dropdown[A]] {

  def withLabel(label: String): Dropdown[A] =
    this.copy(label = Some(label))

  def withoutLabel: Dropdown[A] =
    this.copy(label = None)

  def withObserver(writable: WritableVar[A]): Dropdown[A] =
    this.copy(observers = writable +: observers)

  private[gooey] def build(algebra: Dropdown.Algebra)(
      env: algebra.Env
  ): algebra.UI[Option[A]] =
    algebra.dropdown(label, choices)(env)
}
object Dropdown {
  trait Algebra extends gooey.Algebra {
    def dropdown[A](
        label: Option[String],
        choices: Iterable[(String, A)]
    )(env: Env): UI[Option[A]]
  }

  def apply[A](choices: Iterable[(String, A)]): Dropdown[A] =
    Dropdown(None, choices, Chain.empty)
}
