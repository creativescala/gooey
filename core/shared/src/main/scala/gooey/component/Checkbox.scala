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

/** A [[Checkbox]] allows the user to select or deselect a single option. */
final case class Checkbox(
    label: Option[String],
    default: Boolean,
    observers: Chain[WritableVar[Boolean]]
) extends Component[Checkbox.Algebra, Boolean],
      Labelable[Checkbox] {
  def withLabel(label: String): Checkbox =
    this.copy(label = Some(label))

  def withoutLabel: Checkbox =
    this.copy(label = None)

  def withDefault(default: Boolean): Checkbox =
    this.copy(default = default)

  def withObserver(writable: WritableVar[Boolean]): Checkbox =
    this.copy(observers = writable +: observers)

  private[gooey] def build(algebra: Checkbox.Algebra)(
      env: algebra.Env
  ): algebra.UI[Boolean] =
    algebra.checkbox(label, default, observers)(env)
}
object Checkbox {
  trait Algebra extends gooey.Algebra {
    def checkbox(
        label: Option[String],
        default: Boolean,
        observers: Chain[WritableVar[Boolean]]
    )(env: Env): UI[Boolean]
  }

  val empty: Checkbox = Checkbox(None, false, Chain.empty)
}
